package com.tomakeitgo.lisp.operators.list;

import com.tomakeitgo.lisp.Interpreter;
import com.tomakeitgo.lisp.SContext;
import com.tomakeitgo.lisp.SExpression;

import java.util.List;

public class ListRestOperator extends SExpression.Lambda {
    @Override
    public SExpression eval(List<SExpression> rest, Interpreter interpreter, SContext definitions) {
        if (rest.size() != 1) {
            return new SError("rest requires exactly one argument");
        }

        SExpression arg = rest.getFirst();
        if (!(arg instanceof SExpression.SList list)) {
            return new SError("rest requires a list argument");
        }

        if (list.value().isEmpty()) {
            return new SError("rest called on an empty list");
        }

        return new SExpression.SList(list.value().subList(1, list.value().size()));
    }
}
