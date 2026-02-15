package com.tomakeitgo.lisp.operators.control;

import com.tomakeitgo.lisp.Interpreter;
import com.tomakeitgo.lisp.SContext;
import com.tomakeitgo.lisp.SExpression;

import java.util.List;

public class CondOperator extends SExpression.Lambda {
    @Override
    public SExpression eval(List<SExpression> rest, Interpreter interpreter, SContext definitions) {
        if (rest.isEmpty()) {
            return new SError("cond requires at least one clause");
        }

        for (SExpression clause : rest) {
            if (!(clause instanceof SExpression.SList pair) || pair.value().size() != 2) {
                return new SError("each cond clause must be a list of (condition expression)");
            }
            if (Interpreter.TRUE.equals(interpreter.eval(pair.value().get(0), definitions))) {
                return interpreter.eval(pair.value().get(1), definitions);
            }
        }

        return new SError("no matching cond clause");
    }
}
