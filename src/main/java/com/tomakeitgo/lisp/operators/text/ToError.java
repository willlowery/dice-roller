package com.tomakeitgo.lisp.operators.text;

import com.tomakeitgo.lisp.Interpreter;
import com.tomakeitgo.lisp.SContext;
import com.tomakeitgo.lisp.SExpression;

import java.util.List;

public class ToError implements SExpression.Operator {
    @Override
    public SExpression eval(List<SExpression> rest, Interpreter interpreter, SContext definitions) {
        if (rest.size() != 1)
            return new SError("requires one argument.");
        if (rest.get(0) instanceof SText s) {
            return new SError(s.value());

        }
        return new SError("Unable to make error from type");
    }
}
