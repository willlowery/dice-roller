package com.tomakeitgo.dice;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ParserTest {

    @Test
    void testEmptyInputs() {
        assertEquals("", parseAndEval(null).description());
        assertEquals("", parseAndEval("").description());
        assertEquals("", parseAndEval("       ").description());
    }

    @Test
    void testSingleValue() {
        assertEquals("1", parseAndEval("1").description());
        assertEquals("1D4(4)", parseAndEval("1d4").description());
    }

    @Test
    void testAddition() {
        assertEquals(15, parseAndEval("+ 1D1 1D2 1D3 1D4 1D5").value());
        assertEquals("+ 1 1", parseAndEval("+ 1 1").description());
        assertEquals(3, parseAndEval("+ 1 1 1").value());
        assertEquals("+ 1D4(4) 1", parseAndEval("+ 1D4 1").description());
        assertEquals("+ 1 1D4(4)", parseAndEval("+ 1 1D4").description());
        assertEquals("+ 1D4(4) 1D4(4)", parseAndEval("+ 1D4 1D4").description());
        assertEquals(8, parseAndEval("+ 1D4 1D4").value());

        assertEquals("+ 1 + 2 3", parseAndEval("+ 1 + 2 3").description());
        assertEquals("+ 1 + 1D4(4) 3", parseAndEval("+ 1 + 1d4 3").description());
    }
    
    @Test
    void testSubtraction(){
        assertEquals(-1, parseAndEval("- 1 1 1").value());
        assertEquals("- 1 1 1", parseAndEval("- 1 1 1").description());
        assertEquals("- 1D4(4) 1", parseAndEval("- 1D4 1").description());
        assertEquals("- 1 1D4(4)", parseAndEval("- 1 1D4").description());
        assertEquals("- 1D4(4) 1D4(4)", parseAndEval("- 1D4 1D4").description());
    }

    private static Parser.DiceExpression.Result parseAndEval(String expression) {
        var result = new Parser().parse(new Lexer().lex(expression));
        return result.eval(max -> max);
    }
}