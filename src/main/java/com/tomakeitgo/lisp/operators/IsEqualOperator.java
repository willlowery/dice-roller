package com.tomakeitgo.lisp.operators;

import com.tomakeitgo.lisp.Interpreter;
import com.tomakeitgo.lisp.SContext;
import com.tomakeitgo.lisp.SExpression;

import java.util.List;

public class IsEqualOperator extends SExpression.Lambda {
    @Override
    public SExpression eval(List<SExpression> rest, Interpreter interpreter, SContext definitions) {
        if (rest.size() < 2) return new Error("isEqual requires at least two arguments");

        SExpression s = rest.getFirst();
        for (SExpression sExpression : rest.subList(1, rest.size())) {
            if (!sExpression.equals(s)) {
                return Interpreter.FALSE;
            }
        }
        return Interpreter.TRUE;
    }
}
