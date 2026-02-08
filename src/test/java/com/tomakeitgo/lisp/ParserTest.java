package com.tomakeitgo.lisp;

import com.tomakeitgo.lisp.SExpression.SAtom;
import com.tomakeitgo.lisp.SExpression.SList;
import com.tomakeitgo.lisp.SExpression.SText;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ParserTest {

    @Test
    void test() {
        assertNull(getParse(""));
        assertEquals(new SAtom("hi"), getParse("hi"));
        assertEquals(new SText("hi"), getParse("'hi'"));
        assertEquals(new SList(List.of()), getParse("()"));
        assertEquals(new SList(List.of(new SAtom("+"), new SText("1"), new SText("2"))), getParse("( + '1' '2' )"));
        assertEquals(new SList(List.of(
                new SAtom("a"),
                new SText("b")
        )), getParse("( a 'b' )"));
        assertEquals(new SList(List.of(
                new SList(List.of()),
                new SList(List.of())
        )), getParse("( () () )"));
    }
    
    @Test
    void boop(){
        System.out.println(getParse("hi"));
        
        //(eval a)
    }

    private static SExpression getParse(String expr) {
        return new Parser().parse(new Lexer().lex(expr));
    }

}