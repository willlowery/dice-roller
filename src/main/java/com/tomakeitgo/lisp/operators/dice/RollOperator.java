package com.tomakeitgo.lisp.operators.dice;

import com.tomakeitgo.dice.Lexer;
import com.tomakeitgo.dice.Parser;
import com.tomakeitgo.lisp.Interpreter;
import com.tomakeitgo.lisp.SContext;
import com.tomakeitgo.lisp.SExpression;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;

public class RollOperator extends SExpression.Lambda {
    private static final Random random = new Random();

    @Override
    public SExpression eval(List<SExpression> rest, Interpreter interpreter, SContext definitions) {
        var rollText = ((SExpression.SText) (rest).getFirst()).value();
        var e = new Parser().parse(new Lexer().lex(rollText));
        var result = e.eval((side) -> random.nextInt(1, side + 1));
        return new SExpression.SList(List.of(
                new SNumber(new BigDecimal(result.value())), 
                new SText(result.description() + ": " + result.value()),
                new SNumber(result.min()),
                new SNumber(result.max())
        ));
    }
}
