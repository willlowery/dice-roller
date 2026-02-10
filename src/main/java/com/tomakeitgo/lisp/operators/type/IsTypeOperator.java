package com.tomakeitgo.lisp.operators.type;

import com.tomakeitgo.lisp.Interpreter;
import com.tomakeitgo.lisp.SContext;
import com.tomakeitgo.lisp.SExpression;

import java.util.List;

public class IsTypeOperator extends SExpression.Lambda {
    private final Class<? extends SExpression> type;

    public IsTypeOperator(Class<? extends SExpression> type) {
        this.type = type;
    }

    @Override
    public SExpression eval(List<SExpression> rest, Interpreter interpreter, SContext definitions) {
        if (rest.size() != 1) return new Error("requires one argument.");
        return type.isInstance(rest.getFirst()) ? Interpreter.TRUE : Interpreter.FALSE;
    }
}
