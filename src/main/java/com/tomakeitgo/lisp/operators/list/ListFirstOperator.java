package com.tomakeitgo.lisp.operators.list;

import com.tomakeitgo.lisp.Interpreter;
import com.tomakeitgo.lisp.SContext;
import com.tomakeitgo.lisp.SExpression;

import java.util.List;

public class ListFirstOperator extends SExpression.Lambda {
    @Override
    public SExpression eval(List<SExpression> rest, Interpreter interpreter, SContext definitions) {
        if (rest.size() != 1) {
            return new SExpression.Error("first requires exactly one argument");
        }

        SExpression arg = rest.getFirst();
        if (!(arg instanceof SExpression.SList list)) {
            return new SExpression.Error("first requires a list argument");
        }

        if (list.value().isEmpty()) {
            return new SExpression.Error("first called on an empty list");
        }

        return list.value().getFirst();
    }
}
