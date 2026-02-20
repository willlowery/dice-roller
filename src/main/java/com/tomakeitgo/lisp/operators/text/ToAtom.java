package com.tomakeitgo.lisp.operators.text;

import com.tomakeitgo.lisp.Interpreter;
import com.tomakeitgo.lisp.SContext;
import com.tomakeitgo.lisp.SExpression;

import java.util.List;

public class ToAtom implements SExpression.Operator {
    @Override
    public SExpression eval(List<SExpression> rest, Interpreter interpreter, SContext definitions) {
        if (rest.size() != 1) {
            return new SError("toAtom requires exactly one argument of type text");
        }
        
        if (rest.getFirst() instanceof SText t) {
            return new SAtom(t.value());
        } else {
            return new SError("toAtom requires exactly one argument of type text");
        }
    }
}
