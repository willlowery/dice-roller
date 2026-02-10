package com.tomakeitgo.lisp;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InterpreterTest {

    @ParameterizedTest
    @CsvSource(
            quoteCharacter = '|',
            value = {
                    "42,        NUMBER,  42",
                    "'hello',   TEXT,    hello",
                    "(),        LIST,    ",
                    "foo,       ATOM,    foo",
            })
    void selfEvaluating(String input, String type, String value) {
        var result = eval(input);
        switch (type) {
            case "NUMBER" -> assertEquals(new SExpression.SNumber(new BigDecimal(value)), result);
            case "TEXT" -> assertEquals(new SExpression.SText(value), result);
            case "LIST" -> assertEquals(new SExpression.SList(java.util.List.of()), result);
            case "ATOM" -> assertEquals(new SExpression.SAtom(value), result);
        }
    }

    @ParameterizedTest
    @CsvSource({
            "(if true 1 2),              1",
            "(if false 1 2),             2",
            "(if (isEqual 1 1) 1 2),     1",
            "(if (isEqual 1 2) 1 2),     2",
    })
    void ifBranches(String input, String expected) {
        assertEquals(new SExpression.SNumber(new BigDecimal(expected)), eval(input));
    }

    @ParameterizedTest
    @CsvSource({
            "(isEqual 1 1),  true",
            "(isEqual 1 2),  false",
    })
    void isEqual(String input, String expected) {
        assertEquals(new SExpression.SAtom(expected), eval(input));
    }

    @ParameterizedTest
    @CsvSource({
            "(number/add 2 3),     5",
            "(number/sub 5 3),     2",
            "(number/mul 3 4),     12",
            "(number/div 12 4),    3",
            "(number/mod 7 3),     1",
            "(number/divInt 7 2),  3",
    })
    void arithmetic(String input, String expected) {
        assertEquals(new SExpression.SNumber(new BigDecimal(expected)), eval(input));
    }

    @ParameterizedTest
    @CsvSource({
            "(number/add 'a' 1)",
            "(number/add 1)",
            "(number/mul 'a' 1)",
            "(number/mul 1)",
            "(number/div 'a' 1)",
            "(number/div 1)",
            "(number/mod 'a' 1)",
            "(number/mod 1)",
            "(number/divInt 'a' 1)",
            "(number/divInt 1)",
            "(type/isNumber 1 2)",
            "(isEqual 1)",
            "(if true 1)",
    })
    void errorCases(String input) {
        assertInstanceOf(SExpression.Error.class, eval(input));
    }

    @ParameterizedTest
    @CsvSource({
            "(type/isNumber 42),    true",
            "(type/isText 'hi'),    true",
            "(type/isAtom foo),     true",
            "(type/isList ()),      true",
            "(type/isNumber 'hi'),  false",
    })
    void typeChecks(String input, String expected) {
        assertEquals(new SExpression.SAtom(expected), eval(input));
    }

    @Test
    void defAndReference() {
        var interpreter = new Interpreter();
        var ctx = Interpreter.createSContext();
        interpreter.eval(parse("(def x 5)"), ctx);
        assertEquals(new SExpression.SNumber(new BigDecimal("5")), interpreter.eval(parse("x"), ctx));
    }

    @Test
    void defRedefine() {
        var interpreter = new Interpreter();
        var ctx = Interpreter.createSContext();
        interpreter.eval(parse("(def x 1)"), ctx);
        interpreter.eval(parse("(def x 2)"), ctx);
        assertEquals(new SExpression.SNumber(new BigDecimal("2")), interpreter.eval(parse("x"), ctx));
    }

    @Test
    void lambdaIdentity() {
        var interpreter = new Interpreter();
        var ctx = Interpreter.createSContext();
        interpreter.eval(parse("(def id (lambda (x) x))"), ctx);
        assertEquals(new SExpression.SNumber(new BigDecimal("7")), interpreter.eval(parse("(id 7)"), ctx));
    }

    @Test
    void lambdaMultiArg() {
        var interpreter = new Interpreter();
        var ctx = Interpreter.createSContext();
        interpreter.eval(parse("(def add (lambda (a b) (number/add a b)))"), ctx);
        assertEquals(new SExpression.SNumber(new BigDecimal("5")), interpreter.eval(parse("(add 2 3)"), ctx));
    }

    @Test
    void lambdaCapturesScope() {
        var interpreter = new Interpreter();
        var ctx = Interpreter.createSContext();
        interpreter.eval(parse("(def y 10)"), ctx);
        interpreter.eval(parse("(def getY (lambda () y))"), ctx);
        assertEquals(new SExpression.SNumber(new BigDecimal("10")), interpreter.eval(parse("(getY)"), ctx));
    }

    @Test
    void ifWrongArity() {
        assertInstanceOf(SExpression.Error.class, eval("(if true 1)"));
    }

    @Test
    void ifInLambda() {
        assertEquals(Interpreter.TRUE, eval("((lambda (x) (if (isEqual x 1) true false)) 1)"));
        assertEquals(Interpreter.FALSE, eval("((lambda (x) (if (isEqual x 1) true false)) 0)"));
    }

    @Test
    void isEqualTooFewArgs() {
        assertInstanceOf(SExpression.Error.class, eval("(isEqual 1)"));
    }

    @Test
    void numberAddDecimal() {
        var result = eval("(number/add 0.1 0.2)");
        assertInstanceOf(SExpression.SNumber.class, result);
        assertEquals(0, new BigDecimal("0.3").compareTo(((SExpression.SNumber) result).value()));
    }

    @Test
    void textConcat() {
        assertEquals(new SExpression.SText("hello world"), eval("(text/concat 'hello' ' world')"));
    }

    @Test
    void textConcatSkipsNonText() {
        assertEquals(new SExpression.SText("ab"), eval("(text/concat 'a' 42 'b')"));
    }

    @Test
    void nestedCalls() {
        assertEquals(new SExpression.SNumber(new BigDecimal("6")), eval("(number/add (number/add 1 2) 3)"));
    }

    @Test
    void recursiveLambda() {
        var interpreter = new Interpreter();
        var ctx = Interpreter.createSContext();
        interpreter.eval(parse("(def countdown (lambda (n) (if (isEqual n 0 ) 0 (countdown (number/sub n 1 )) )))"), ctx);
        assertEquals(new SExpression.SNumber(new BigDecimal("0")), interpreter.eval(parse("(countdown 5)"), ctx));
    }

    @Test
    void listAppendSingle() {
        var result = eval("(list/append () 1)");
        assertEquals(new SExpression.SList(java.util.List.of(new SExpression.SNumber(new BigDecimal("1")))), result);
    }

    @Test
    void listAppendMultiple() {
        var result = eval("(list/append () 1 2 3)");
        assertEquals(new SExpression.SList(java.util.List.of(
                new SExpression.SNumber(new BigDecimal("1")),
                new SExpression.SNumber(new BigDecimal("2")),
                new SExpression.SNumber(new BigDecimal("3"))
        )), result);
    }

    @Test
    void listAppendToExisting() {
        var result = eval("((lambda (xs) (list/append xs 3)) (list/append () 1 2))");
        assertEquals(new SExpression.SList(List.of(
                new SExpression.SNumber(new BigDecimal("1")),
                new SExpression.SNumber(new BigDecimal("2")),
                new SExpression.SNumber(new BigDecimal("3"))
        )), result);
    }

    @Test
    void listAppendNotAList() {
        assertInstanceOf(SExpression.Error.class, eval("(list/append 1 2)"));
    }

    @Test
    void listAppendTooFewArgs() {
        assertInstanceOf(SExpression.Error.class, eval("(list/append ())"));
    }

    @Test
    void isEmptyOnEmptyList() {
        assertEquals(Interpreter.TRUE, eval("(list/isEmpty ())"));
    }

    @Test
    void isEmptyOnNonEmptyList() {
        assertEquals(Interpreter.FALSE, eval("(list/isEmpty (list/append () 1))"));
    }

    @Test
    void isEmptyNotAList() {
        assertInstanceOf(SExpression.Error.class, eval("(list/isEmpty 42)"));
    }

    @Test
    void isEmptyTooFewArgs() {
        assertInstanceOf(SExpression.Error.class, eval("(list/isEmpty)"));
    }

    @Test
    void isEmptyTooManyArgs() {
        assertInstanceOf(SExpression.Error.class, eval("(list/isEmpty () ())"));
    }

    @Test
    void firstReturnsFirstItem() {
        assertEquals(new SExpression.SNumber(new BigDecimal("1")), eval("(list/first (list/append () 1 2 3))"));
    }

    @Test
    void firstOnEmptyList() {
        assertInstanceOf(SExpression.Error.class, eval("(list/first ())"));
    }

    @Test
    void firstNotAList() {
        assertInstanceOf(SExpression.Error.class, eval("(list/first 42)"));
    }

    @Test
    void firstTooFewArgs() {
        assertInstanceOf(SExpression.Error.class, eval("(list/first)"));
    }

    @Test
    void firstTooManyArgs() {
        assertInstanceOf(SExpression.Error.class, eval("(list/first (list/append () 1) (list/append () 2))"));
    }

    @Test
    void restReturnsRemainingItems() {
        var result = eval("(list/rest (list/append () 1 2 3))");
        assertEquals(new SExpression.SList(List.of(
                new SExpression.SNumber(new BigDecimal("2")),
                new SExpression.SNumber(new BigDecimal("3"))
        )), result);
    }

    @Test
    void restOfSingleElementList() {
        assertEquals(new SExpression.SList(List.of()), eval("(list/rest (list/append () 1))"));
    }

    @Test
    void restOnEmptyList() {
        assertInstanceOf(SExpression.Error.class, eval("(list/rest ())"));
    }

    @Test
    void restNotAList() {
        assertInstanceOf(SExpression.Error.class, eval("(list/rest 42)"));
    }

    @Test
    void restTooFewArgs() {
        assertInstanceOf(SExpression.Error.class, eval("(list/rest)"));
    }

    @Test
    void restTooManyArgs() {
        assertInstanceOf(SExpression.Error.class, eval("(list/rest (list/append () 1) (list/append () 2))"));
    }

    @Test
    void helpReturnsText() {
        var result = eval("(help)");
        assertInstanceOf(SExpression.SText.class, result);
        var text = ((SExpression.SText) result).value();
        assertTrue(text.contains("if"));
        assertTrue(text.contains("isEqual"));
        assertTrue(text.contains("number/add"));
        assertTrue(text.contains("lambda"));
        assertTrue(text.contains("help"));
    }

    private static SExpression eval(String input) {
        return new Interpreter().eval(parse(input), Interpreter.createSContext());
    }

    private static SExpression parse(String input) {
        return new Parser().parse(new Lexer().lex(input));
    }
}
