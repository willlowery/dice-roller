package com.tomakeitgo.lisp.operators;

import com.tomakeitgo.lisp.Interpreter;
import com.tomakeitgo.lisp.SContext;
import com.tomakeitgo.lisp.SExpression;

import java.util.List;

public class EvalOperator extends SExpression.Lambda {

    @Override
    public SExpression eval(List<SExpression> rest, Interpreter interpreter, SContext definitions) {
        SExpression result = new Error("No Items to evaluate");
        for (SExpression expression : rest) {
            result = interpreter.eval(expression, definitions);
        }
        return result;
    }
}
