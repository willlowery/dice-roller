package com.tomakeitgo.lisp.operators.control;

import com.tomakeitgo.lisp.Interpreter;
import com.tomakeitgo.lisp.SContext;
import com.tomakeitgo.lisp.SExpression;

import java.util.List;

public class IfOperator implements SExpression.Operator {
    @Override
    public SExpression eval(List<SExpression> rest, Interpreter interpreter, SContext definitions) {
        if (rest.size() != 3) {
            return new SError("if requires three arguments");
        }
        if (Interpreter.TRUE.equals(interpreter.eval(rest.getFirst(), definitions))) {
            return interpreter.eval(rest.get(1), definitions);
        } else {
            return interpreter.eval(rest.get(2), definitions);
        }
    }
}
