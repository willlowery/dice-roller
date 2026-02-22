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
                of(Interpreter.TRUE, "((lambda (x) (if (isEqual x 1) true false)) 1)"),
                of(Interpreter.FALSE, "((lambda (x) (if (isEqual x 1) true false)) 0)"),

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
                of(new SNumber(new BigDecimal("6")), "(number/add (number/add 1 2) 3)"),
                of(new SNumber(new BigDecimal(".3")), "(number/add 0.1 0.2)"),

                // text/toAtom
                of(new SAtom("text"), "(text/toAtom 'text')"),
                of(new SError("toAtom requires exactly one argument of type text"), "(text/toAtom )"),
                of(new SError("toAtom requires exactly one argument of type text"), "(text/toAtom 1)"),

                // text/fromAtom
                of(new SText("foo"), "(text/fromAtom foo)"),
                of(new SText("true"), "(text/fromAtom true)"),
                of(new SError("fromAtom requires exactly one argument of type atom"), "(text/fromAtom 'already text')"),
                of(new SError("fromAtom requires exactly one argument of type atom"), "(text/fromAtom 42)"),
                of(new SError("fromAtom requires exactly one argument of type atom"), "(text/fromAtom)"),
                of(new SError("fromAtom requires exactly one argument of type atom"), "(text/fromAtom a b)"),

                // text/startsWith
                of(Interpreter.TRUE, "(text/startsWith 'hello' 'hel')"),
                of(Interpreter.FALSE, "(text/startsWith 'hello' 'world')"),
                of(Interpreter.TRUE, "(text/startsWith 'hello' '')"),
                of(Interpreter.FALSE, "(text/startsWith '' 'a')"),
                of(Interpreter.TRUE, "(text/startsWith 'hello' 'hello')"),
                of(new SError("text/startsWith requires exactly two arguments of type text"), "(text/startsWith 'hello' 42)"),
                of(new SError("text/startsWith requires exactly two arguments of type text"), "(text/startsWith 'hello')"),
                of(new SError("text/startsWith requires exactly two arguments of type text"), "(text/startsWith 'a' 'b' 'c')"),

                // text/concat
                of(new SText("hello world"), "(text/concat 'hello' ' world')"),
                of(new SText("ab"), "(text/concat 'a' 42 'b')"),
                of(new SText("n=7"), "(text/concat 'n=' (number/text 7))"),

                // number to text
                of(new SText("42"), "(number/text 42)"),
                of(new SText("3.14"), "(number/text 3.14)"),
                of(new SText("5"), "(number/text 5.00)"),
                of(new SError("number/text requires exactly one argument"), "(number/text)"),
                of(new SError("number/text requires exactly one argument"), "(number/text 1 2)"),
                of(new SError("number/text requires a number argument"), "(number/text 'hello')"),

                // def
                of(new SNumber(BigDecimal.valueOf(5)), "(def x 5) x"),
                of(new SNumber(BigDecimal.valueOf(2)), "(def x 1) (def x 2) x"),

                // lambda
                of(new SNumber(new BigDecimal("7")), "(def id (lambda (x) x)) (id 7)"),
                of(new SNumber(new BigDecimal("5")), "(def add (lambda (a b) (number/add a b))) (add 2 3)"),
                of(new SNumber(new BigDecimal("10")), "(def y 10) (def getY (lambda () y)) (getY)"),
                of(new SNumber(new BigDecimal("0")), "(def countdown (lambda (n) (if (isEqual n 0 ) 0 (countdown (number/sub n 1 )) ))) (countdown 5)"),

                // eval
                of(new SNumber(new BigDecimal("1")), "(eval 1)"),
                of(new SText("1"), "(eval '1')"),

                // cond
                of(new SNumber(new BigDecimal("1")), "(cond (true 1))"),
                of(new SNumber(new BigDecimal("2")), "(cond (false 1) (true 2))"),
                of(new SText("b"), "(cond ((isEqual 1 2) 'a') ((isEqual 1 1) 'b'))"),
                of(new SNumber(new BigDecimal("1")), "(cond (true 1) (true 2))"),

                // list/append
                of(new SList(List.of(new SNumber(new BigDecimal("1")))), "(list/append () 1)"),
                of(new SList(List.of(
                        new SNumber(new BigDecimal("1")),
                        new SNumber(new BigDecimal("2")),
                        new SNumber(new BigDecimal("3"))
                )), "(list/append () 1 2 3)"),
                of(new SList(List.of(
                        new SNumber(new BigDecimal("1")),
                        new SNumber(new BigDecimal("2")),
                        new SNumber(new BigDecimal("3"))
                )), "((lambda (xs) (list/append xs 3)) (list/append () 1 2))"),

                // list/isEmpty
                of(Interpreter.TRUE, "(list/isEmpty ())"),
                of(Interpreter.FALSE, "(list/isEmpty (list/append () 1))"),

                // list/first
                of(new SNumber(new BigDecimal("1")), "(list/first (list/append () 1 2 3))"),

                // list/rest
                of(new SList(List.of(
                        new SNumber(new BigDecimal("2")),
                        new SNumber(new BigDecimal("3"))
                )), "(list/rest (list/append () 1 2 3))"),
                of(new SList(List.of()), "(list/rest (list/append () 1))"),

                // list/nth
                of(new SNumber(new BigDecimal("1")), "(list/nth (list 1 2 3) 0)"),
                of(new SNumber(new BigDecimal("3")), "(list/nth (list 1 2 3) 2)"),
                of(new SError("list/nth index out of bounds"), "(list/nth (list 1 2 3) 3)"),
                of(new SError("list/nth index out of bounds"), "(list/nth (list 1 2 3) -1)"),
                of(new SError("list/nth requires a list as the first argument"), "(list/nth 42 0)"),
                of(new SError("list/nth requires a number as the second argument"), "(list/nth (list 1) 'a')"),
                of(new SError("list/nth requires exactly two arguments"), "(list/nth)"),

                // list/contains
                of(Interpreter.TRUE, "(list/contains (list 1 2 3) 2)"),
                of(Interpreter.FALSE, "(list/contains (list 1 2 3) 5)"),
                of(Interpreter.FALSE, "(list/contains () 1)"),
                of(Interpreter.TRUE, "(list/contains (list 'a' 'b') 'a')"),
                of(new SError("list/contains requires a list as the first argument"), "(list/contains 42 1)"),
                of(new SError("list/contains requires exactly two arguments"), "(list/contains (list 1))"),
                of(new SError("list/contains requires exactly two arguments"), "(list/contains (list 1) 1 2)")
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
                of("(if true 1)"),
                of("(eval)"),
                of("(cond (false 1))"),
                of("(cond true)"),
                of("(cond)"),
                of("(list/append 1 2)"),
                of("(list/append ())"),
                of("(list/isEmpty 42)"),
                of("(list/isEmpty)"),
                of("(list/isEmpty () ())"),
                of("(list/first ())"),
                of("(list/first 42)"),
                of("(list/first)"),
                of("(list/first (list/append () 1) (list/append () 2))"),
                of("(list/rest ())"),
                of("(list/rest 42)"),
                of("(list/rest)"),
                of("(list/rest (list/append () 1) (list/append () 2))")
        );
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
        Interpreter interpreter = new Interpreter();
        var context = interpreter.createSContext();

        SExpression result = null;
        for (SExpression expression : toEval) {
            result = interpreter.eval(expression, context);
        }

        return result;
    }
}
