package com.tomakeitgo.lisp.operators.control;

import com.tomakeitgo.lisp.Interpreter;
import com.tomakeitgo.lisp.SContext;
import com.tomakeitgo.lisp.SExpression;

import java.util.List;

public class AndOperator implements SExpression.Operator {
    @Override
    public SExpression eval(List<SExpression> rest, Interpreter interpreter, SContext definitions) {
        if (rest.size() < 2) {
            return new SError("and requires at least two arguments");
        }
        for (SExpression expression : rest) {
            if (!Interpreter.TRUE.equals(interpreter.eval(expression, definitions))) {
                return Interpreter.FALSE;
            }
        }
        return Interpreter.TRUE;
    }
}
