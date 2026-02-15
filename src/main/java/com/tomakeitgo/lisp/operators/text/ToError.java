package com.tomakeitgo.lisp.operators.text;

import com.tomakeitgo.lisp.Interpreter;
import com.tomakeitgo.lisp.SContext;
import com.tomakeitgo.lisp.SExpression;

import java.util.List;

public class ToError extends SExpression.Lambda {
    @Override
    public SExpression eval(List<SExpression> rest, Interpreter interpreter, SContext definitions) {
        if (rest.size() != 1)
            return new Error("requires one argument.");
        if (rest.get(0) instanceof SText s) {
            return new Error(s.value());

        }
        return new Error("Unable to make error from type");
    }
}
