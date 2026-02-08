package com.tomakeitgo.lisp;


import com.tomakeitgo.lisp.Lexer.Token;
import com.tomakeitgo.lisp.Lexer.Token.Type;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LexerTest {
    
    @Test
    void testNumber(){
        assertEquals(List.of(new Token(Type.NUMBER, "1")), lex("1"));
        assertEquals(List.of(new Token(Type.NUMBER, "101")), lex("101"));
        assertEquals(List.of(new Token(Type.NUMBER, "-101")), lex("-101"));
        assertEquals(List.of(new Token(Type.NUMBER, "1.0")), lex("1.0"));
    }
    
    @Test
    void string(){
        assertEquals(List.of(new Token(Type.STRING, "")), lex("''"));
        assertEquals(List.of(new Token(Type.STRING, " ")), lex("' '"));
        assertEquals(List.of(new Token(Type.STRING, "hi")), lex("'hi'"));
        assertEquals(List.of(new Token(Type.STRING, "'")), lex("''''"));
    }
    
    @Test
    void test(){
        assertEquals(List.of(new Token(Type.PAREN_OPEN, "(")), lex("("));
        assertEquals(List.of(new Token(Type.PAREN_CLOSE, ")")), lex(")"));
        assertEquals(List.of(new Token(Type.ATOM, "apple")), lex("apple"));
        
        assertEquals(List.of(
                new Token(Type.PAREN_OPEN, "("),
                new Token(Type.PAREN_CLOSE, ")")
        ), lex("()"));
        
        assertEquals(List.of(
                new Token(Type.PAREN_OPEN, "("),
                new Token(Type.ATOM, "+"),
                new Token(Type.NUMBER, "1"),
                new Token(Type.NUMBER, "1"),
                new Token(Type.PAREN_CLOSE, ")")
        ), lex("(+ 1 1)"));

    }

    private static List<Token> lex(String input) {
        return new Lexer().lex(input);
    }
}