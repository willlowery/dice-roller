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
        assertEquals("1D4(0)", parseAndEval("1d4").description());
    }

    @Test
    void testAddition() {
        assertEquals("+ 1 1", parseAndEval("+ 1 1").description());
        assertEquals("+ 1D4(0) 1", parseAndEval("+ 1D4 1").description());
        assertEquals("+ 1 1D4(0)", parseAndEval("+ 1 1D4").description());
        assertEquals("+ 1D4(0) 1D4(0)", parseAndEval("+ 1D4 1D4").description());
        
        assertEquals("+ 1 + 2 3", parseAndEval("+ 1 + 2 3").description());
        assertEquals("+ 1 + 1D4(0) 3", parseAndEval("+ 1 + 1d4 3").description());
    }
    
    @Test
    void testSubtraction(){
        assertEquals("- 1 1", parseAndEval("- 1 1").description());
        assertEquals("- 1D4(0) 1", parseAndEval("- 1D4 1").description());
        assertEquals("- 1 1D4(0)", parseAndEval("- 1 1D4").description());
        assertEquals("- 1D4(0) 1D4(0)", parseAndEval("- 1D4 1D4").description());
    }

    private static Parser.DiceExpression.Result parseAndEval(String expression) {
        var result = new Parser().parse(new Lexer().lex(expression));
        return result.eval(max -> 0);
    }
}