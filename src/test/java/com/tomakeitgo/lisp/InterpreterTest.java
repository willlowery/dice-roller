package com.tomakeitgo.lisp;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

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
    })
    void arithmetic(String input, String expected) {
        assertEquals(new SExpression.SNumber(new BigDecimal(expected)), eval(input));
    }

    @ParameterizedTest
    @CsvSource({
            "(number/add 'a' 1)",
            "(number/add 1)",
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

    private static SExpression eval(String input) {
        return new Interpreter().eval(parse(input), Interpreter.createSContext());
    }

    private static SExpression parse(String input) {
        return new Parser().parse(new Lexer().lex(input));
    }
}
