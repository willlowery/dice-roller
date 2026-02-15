package com.tomakeitgo.lisp;

import com.tomakeitgo.lisp.SExpression.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.of;

class InterpreterTest {

    @ParameterizedTest
    @MethodSource("testInterpreterInputs")
    void testInterpreter(SExpression expected, String input) {
        assertEquals(expected, eval(input));
    }

    static Stream<Arguments> testInterpreterInputs() {
        return Stream.of(
                // self evaluations
                of(new SNumber(new BigDecimal("42")), "42"),
                of(new SText("hello"), "'hello'"),
                of(new SList(List.of()), "()"),
                of(new SAtom("foo"), "foo"),

                // if
                of(new SNumber(new BigDecimal("1")), "(if true 1 2)"),
                of(new SNumber(new BigDecimal("2")), "(if false 1 2)"),
                of(new SNumber(new BigDecimal("1")), "(if (isEqual 1 1) 1 2)"),
                of(new SNumber(new BigDecimal("2")), "(if (isEqual 1 2) 1 2)"),

                // isEqual
                of(new SAtom("true"), "(isEqual 1 1)"),
                of(new SAtom("false"), "(isEqual 1 2)"),
                of(new SAtom("false"), "(isEqual 1 2)"),
                of(new SError("isEqual requires at least two arguments"), "(isEqual 1)"),

                // type checks
                of(new SAtom("true"), "(type/isNumber 42)"),
                of(new SAtom("true"), "(type/isText 'hi')"),
                of(new SAtom("true"), "(type/isAtom foo)"),
                of(new SAtom("true"), "(type/isList ())"),
                of(new SAtom("false"), "(type/isNumber 'hi')"),

                // number/operators
                of(new SNumber(new BigDecimal("5")), "(number/add 2 3)"),
                of(new SNumber(new BigDecimal("2")), "(number/sub 5 3)"),
                of(new SNumber(new BigDecimal("12")), "(number/mul 3 4)"),
                of(new SNumber(new BigDecimal("3")), "(number/div 12 4)"),
                of(new SNumber(new BigDecimal("1")), "(number/mod 7 3)"),
                of(new SNumber(new BigDecimal("3")), "(number/divInt 7 2)"),

                // text/toAtom
                of(new SAtom("text"), "(text/toAtom 'text')"),
                of(new SError("toAtom requires exactly one argument of type text"), "(text/toAtom )"),
                of(new SError("toAtom requires exactly one argument of type text"), "(text/toAtom 1)"),

                // number to text
                of(new SText("42"), "(number/text 42)"),
                of(new SText("3.14"), "(number/text 3.14)"),
                of(new SText("5"), "(number/text 5.00)"),
                of(new SError("number/text requires exactly one argument"), "(number/text)"),
                of(new SError("number/text requires exactly one argument"), "(number/text 1 2)"),
                of(new SError("number/text requires a number argument"), "(number/text 'hello')"),
                // def
                of(new SNumber(BigDecimal.valueOf(5)), "(def x 5) x"),
                of(new SNumber(BigDecimal.valueOf(2)), "(def x 1) (def x 2) x")
        );
    }


    @ParameterizedTest
    @MethodSource("errorCasesInputs")
    void errorCases(String input) {
        assertInstanceOf(SError.class, eval(input));
    }

    static Stream<Arguments> errorCasesInputs() {
        return Stream.of(
                of("(number/add 'a' 1)"),
                of("(number/add 1)"),
                of("(number/mul 'a' 1)"),
                of("(number/mul 1)"),
                of("(number/div 'a' 1)"),
                of("(number/div 1)"),
                of("(number/mod 'a' 1)"),
                of("(number/mod 1)"),
                of("(number/divInt 'a' 1)"),
                of("(number/divInt 1)"),
                of("(type/isNumber 1 2)"),
                of("(isEqual 1)"),
                of("(if true 1)")
        );
    }
    
    @Test
    void lambdaIdentity() {
        var interpreter = new Interpreter();
        var ctx = Interpreter.createSContext();
        interpreter.eval(parse("(def id (lambda (x) x))"), ctx);
        assertEquals(new SNumber(new BigDecimal("7")), interpreter.eval(parse("(id 7)"), ctx));
    }

    @Test
    void lambdaMultiArg() {
        var interpreter = new Interpreter();
        var ctx = Interpreter.createSContext();
        interpreter.eval(parse("(def add (lambda (a b) (number/add a b)))"), ctx);
        assertEquals(new SNumber(new BigDecimal("5")), interpreter.eval(parse("(add 2 3)"), ctx));
    }

    @Test
    void lambdaCapturesScope() {
        var interpreter = new Interpreter();
        var ctx = Interpreter.createSContext();
        interpreter.eval(parse("(def y 10)"), ctx);
        interpreter.eval(parse("(def getY (lambda () y))"), ctx);
        assertEquals(new SNumber(new BigDecimal("10")), interpreter.eval(parse("(getY)"), ctx));
    }

    @Test
    void ifWrongArity() {
        assertInstanceOf(SError.class, eval("(if true 1)"));
    }

    @Test
    void ifInLambda() {
        assertEquals(Interpreter.TRUE, eval("((lambda (x) (if (isEqual x 1) true false)) 1)"));
        assertEquals(Interpreter.FALSE, eval("((lambda (x) (if (isEqual x 1) true false)) 0)"));
    }


    @Test
    void numberAddDecimal() {
        var result = eval("(number/add 0.1 0.2)");
        assertInstanceOf(SNumber.class, result);
        assertEquals(0, new BigDecimal("0.3").compareTo(((SNumber) result).value()));
    }

    @Test
    void textConcat() {
        assertEquals(new SText("hello world"), eval("(text/concat 'hello' ' world')"));
    }

    @Test
    void textConcatSkipsNonText() {
        assertEquals(new SText("ab"), eval("(text/concat 'a' 42 'b')"));
    }

    @Test
    void nestedCalls() {
        assertEquals(new SNumber(new BigDecimal("6")), eval("(number/add (number/add 1 2) 3)"));
    }

    @Test
    void recursiveLambda() {
        var interpreter = new Interpreter();
        var ctx = Interpreter.createSContext();
        interpreter.eval(parse("(def countdown (lambda (n) (if (isEqual n 0 ) 0 (countdown (number/sub n 1 )) )))"), ctx);
        assertEquals(new SNumber(new BigDecimal("0")), interpreter.eval(parse("(countdown 5)"), ctx));
    }

    @Test
    void listAppendSingle() {
        var result = eval("(list/append () 1)");
        assertEquals(new SList(java.util.List.of(new SNumber(new BigDecimal("1")))), result);
    }

    @Test
    void listAppendMultiple() {
        var result = eval("(list/append () 1 2 3)");
        assertEquals(new SList(java.util.List.of(
                new SNumber(new BigDecimal("1")),
                new SNumber(new BigDecimal("2")),
                new SNumber(new BigDecimal("3"))
        )), result);
    }

    @Test
    void listAppendToExisting() {
        var result = eval("((lambda (xs) (list/append xs 3)) (list/append () 1 2))");
        assertEquals(new SList(List.of(
                new SNumber(new BigDecimal("1")),
                new SNumber(new BigDecimal("2")),
                new SNumber(new BigDecimal("3"))
        )), result);
    }

    @Test
    void listAppendNotAList() {
        assertInstanceOf(SError.class, eval("(list/append 1 2)"));
    }

    @Test
    void listAppendTooFewArgs() {
        assertInstanceOf(SError.class, eval("(list/append ())"));
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
        assertInstanceOf(SError.class, eval("(list/isEmpty 42)"));
    }

    @Test
    void isEmptyTooFewArgs() {
        assertInstanceOf(SError.class, eval("(list/isEmpty)"));
    }

    @Test
    void isEmptyTooManyArgs() {
        assertInstanceOf(SError.class, eval("(list/isEmpty () ())"));
    }

    @Test
    void firstReturnsFirstItem() {
        assertEquals(new SNumber(new BigDecimal("1")), eval("(list/first (list/append () 1 2 3))"));
    }

    @Test
    void firstOnEmptyList() {
        assertInstanceOf(SError.class, eval("(list/first ())"));
    }

    @Test
    void firstNotAList() {
        assertInstanceOf(SError.class, eval("(list/first 42)"));
    }

    @Test
    void firstTooFewArgs() {
        assertInstanceOf(SError.class, eval("(list/first)"));
    }

    @Test
    void firstTooManyArgs() {
        assertInstanceOf(SError.class, eval("(list/first (list/append () 1) (list/append () 2))"));
    }

    @Test
    void restReturnsRemainingItems() {
        var result = eval("(list/rest (list/append () 1 2 3))");
        assertEquals(new SList(List.of(
                new SNumber(new BigDecimal("2")),
                new SNumber(new BigDecimal("3"))
        )), result);
    }

    @Test
    void restOfSingleElementList() {
        assertEquals(new SList(List.of()), eval("(list/rest (list/append () 1))"));
    }

    @Test
    void restOnEmptyList() {
        assertInstanceOf(SError.class, eval("(list/rest ())"));
    }

    @Test
    void restNotAList() {
        assertInstanceOf(SError.class, eval("(list/rest 42)"));
    }

    @Test
    void restTooFewArgs() {
        assertInstanceOf(SError.class, eval("(list/rest)"));
    }

    @Test
    void restTooManyArgs() {
        assertInstanceOf(SError.class, eval("(list/rest (list/append () 1) (list/append () 2))"));
    }

    @Test
    void testEval() {
        assertInstanceOf(SError.class, eval("(eval)"));
        assertEquals(eval("1"), eval("(eval 1)"));
        assertEquals(eval("'1'"), eval("(eval '1')"));
    }

    @Test
    void condBasicMatch() {
        assertEquals(new SNumber(new BigDecimal("1")), eval("(cond (true 1))"));
    }

    @Test
    void condMultiBranch() {
        assertEquals(new SNumber(new BigDecimal("2")), eval("(cond (false 1) (true 2))"));
    }

    @Test
    void condWithExpressions() {
        assertEquals(new SText("b"), eval("(cond ((isEqual 1 2) 'a') ((isEqual 1 1) 'b'))"));
    }

    @Test
    void condFirstMatchWins() {
        assertEquals(new SNumber(new BigDecimal("1")), eval("(cond (true 1) (true 2))"));
    }

    @Test
    void condNoMatch() {
        assertInstanceOf(SError.class, eval("(cond (false 1))"));
    }

    @Test
    void condInvalidClause() {
        assertInstanceOf(SError.class, eval("(cond true)"));
    }

    @Test
    void condNoClauses() {
        assertInstanceOf(SError.class, eval("(cond)"));
    }


    @Test
    void numberToTextComposable() {
        assertEquals(new SText("n=7"), eval("(text/concat 'n=' (number/text 7))"));
    }

    @Test
    void helpReturnsText() {
        var result = eval("(help)");
        assertInstanceOf(SText.class, result);
        var text = ((SText) result).value();
        assertTrue(text.contains("if"));
        assertTrue(text.contains("isEqual"));
        assertTrue(text.contains("number/add"));
        assertTrue(text.contains("lambda"));
        assertTrue(text.contains("help"));
    }

    private static SExpression eval(String input) {
        var toEval = new Parser().parseAll(new Lexer().lex(input));
        var context = Interpreter.createSContext();

        SExpression result = null;
        for (SExpression expression : toEval) {
            result = new Interpreter().eval(expression, context);
        }

        return result;
    }

    private static SExpression parse(String input) {
        return new Parser().parse(new Lexer().lex(input));
    }
}
