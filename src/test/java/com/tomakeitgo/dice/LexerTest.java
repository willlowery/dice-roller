package com.tomakeitgo.dice;

import com.tomakeitgo.dice.Lexer.Token;
import com.tomakeitgo.dice.Lexer.Token.Type;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LexerTest {

    @Test
    void emptyString_expectEmptyList() {
        assertEquals(List.of(), lex(null));
        assertEquals(List.of(), lex(""));
    }

    @Test
    void singleSymbol_expectSingleSymbol() {
        assertEquals(tokens(new Token(Type.D, "D")), lex("D"));
        assertEquals(tokens(new Token(Type.D, "D")), lex("d"));

        assertEquals(tokens(new Token(Type.PLUS, "+")), lex("+"));
        assertEquals(tokens(new Token(Type.MINUS, "-")), lex("-"));

        assertEquals(tokens(new Token(Type.DIGITS, "0")), lex("0"));
        assertEquals(tokens(new Token(Type.DIGITS, "01")), lex("01"));
    }

    @Test
    void multipleSymbols_expectMultipleSymbols() {
        assertEquals(tokens(
                        t(Type.DIGITS, "1"),
                        t(Type.D, "D"),
                        t(Type.DIGITS, "4")
                )
                , lex("1d4")
        );
        assertEquals(tokens(
                        t(Type.DIGITS, "1"),
                        t(Type.PLUS, "+"),
                        t(Type.DIGITS, "4")
                )
                , lex("1 + 4")
        );
    }

    List<Token> tokens(Token... tokens) {
        return List.of(tokens);
    }
    
    Token t(Type t, String s) {
        return new Token(t, s);
    }

    private static List<Token> lex(String input) {
        var result = new Lexer().lex(input);
        return result;
    }
}