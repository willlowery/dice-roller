package com.tomakeitgo.dice;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ParserTest {

    @Test
    void testEmptyInputs() {
        assertEquals("", parseAndEval(null).describe());
        assertEquals("", parseAndEval("").describe());
        assertEquals("", parseAndEval("       ").describe());
    }

    @Test
    void testSingleValue() {
        assertEquals("1", parseAndEval("1").describe());
        assertEquals("1D4", parseAndEval("1d4").describe());
    }

    @Test
    void testAddition() {
        assertEquals("+ 1 1", parseAndEval("+ 1 1").describe());
        assertEquals("+ 1D4 1", parseAndEval("+ 1D4 1").describe());
        assertEquals("+ 1 1D4", parseAndEval("+ 1 1D4").describe());
        assertEquals("+ 1D4 1D4", parseAndEval("+ 1D4 1D4").describe());
        
        assertEquals("+ 1 + 2 3", parseAndEval("+ 1 + 2 3").describe());
        assertEquals("+ 1 + 1D4 3", parseAndEval("+ 1 + 1d4 3").describe());
    }
    
    @Test
    void testSubtraction(){
        assertEquals("- 1 1", parseAndEval("- 1 1").describe());
        assertEquals("- 1D4 1", parseAndEval("- 1D4 1").describe());
        assertEquals("- 1 1D4", parseAndEval("- 1 1D4").describe());
        assertEquals("- 1D4 1D4", parseAndEval("- 1D4 1D4").describe());
    }

    private static Parser.DiceExpression.Result parseAndEval(String expression) {
        var result = new Parser().parse(new Lexer().lex(expression));
        return result.eval(max -> 0);
    }
}