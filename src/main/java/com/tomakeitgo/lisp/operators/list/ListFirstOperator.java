package com.tomakeitgo.lisp.operators.list;

import com.tomakeitgo.lisp.Interpreter;
import com.tomakeitgo.lisp.SContext;
import com.tomakeitgo.lisp.SExpression;

import java.util.List;

public class ListFirstOperator implements SExpression.Operator {
    @Override
    public SExpression eval(List<SExpression> rest, Interpreter interpreter, SContext definitions) {
        if (rest.size() != 1) {
            return new SError("first requires exactly one argument");
        }

        SExpression arg = rest.getFirst();
        if (!(arg instanceof SExpression.SList list)) {
            return new SError("first requires a list argument");
        }

        if (list.value().isEmpty()) {
            return new SError("first called on an empty list");
        }

        return list.value().getFirst();
    }
}
