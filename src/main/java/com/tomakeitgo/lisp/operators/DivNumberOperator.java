package com.tomakeitgo.lisp.operators;

import com.tomakeitgo.lisp.Interpreter;
import com.tomakeitgo.lisp.SContext;
import com.tomakeitgo.lisp.SExpression;

import java.util.List;

public class DivNumberOperator extends SExpression.Lambda {
    @Override
    public SExpression eval(List<SExpression> rest, Interpreter interpreter, SContext definitions) {
        if (rest.size() == 2) {
            if (rest.get(0) instanceof SExpression.SNumber first && rest.get(1) instanceof SExpression.SNumber second) {
                return first.div(second);
            } else {
                return new Error("requires two arguments of type Number");
            }
        } else {
            return new Error("requires two arguments of type Number");
        }
    }
}
