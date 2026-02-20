package com.tomakeitgo.lisp.operators.list;

import com.tomakeitgo.lisp.Interpreter;
import com.tomakeitgo.lisp.SContext;
import com.tomakeitgo.lisp.SExpression;

import java.util.List;

public class IsEmptyOperator implements SExpression.Operator {
    @Override
    public SExpression eval(List<SExpression> rest, Interpreter interpreter, SContext definitions) {
        if (rest.size() != 1) {
            return new SError("isEmpty requires exactly one argument");
        }

        SExpression arg = rest.getFirst();
        if (arg instanceof SExpression.SList list) {
            return list.value().isEmpty() ? Interpreter.TRUE : Interpreter.FALSE;
        } else {
            return new SError("isEmpty requires a list argument");
        }
    }
}
