package com.tomakeitgo.lisp.operators.text;

import com.tomakeitgo.lisp.Interpreter;
import com.tomakeitgo.lisp.SContext;
import com.tomakeitgo.lisp.SExpression;

import java.util.List;

public class FromAtom implements SExpression.Operator {
    @Override
    public SExpression eval(List<SExpression> rest, Interpreter interpreter, SContext definitions) {
        if (rest.size() != 1) {
            return new SError("fromAtom requires exactly one argument of type atom");
        }

        if (rest.getFirst() instanceof SAtom a) {
            return new SText(a.value());
        } else {
            return new SError("fromAtom requires exactly one argument of type atom");
        }
    }
}
