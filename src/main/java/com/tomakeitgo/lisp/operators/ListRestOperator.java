package com.tomakeitgo.lisp.operators;

import com.tomakeitgo.lisp.Interpreter;
import com.tomakeitgo.lisp.SContext;
import com.tomakeitgo.lisp.SExpression;

import java.util.List;

public class ListRestOperator extends SExpression.Lambda {
    @Override
    public SExpression eval(List<SExpression> rest, Interpreter interpreter, SContext definitions) {
        if (rest.size() != 1) {
            return new SExpression.Error("rest requires exactly one argument");
        }

        SExpression arg = rest.getFirst();
        if (!(arg instanceof SExpression.SList list)) {
            return new SExpression.Error("rest requires a list argument");
        }

        if (list.value().isEmpty()) {
            return new SExpression.Error("rest called on an empty list");
        }

        return new SExpression.SList(list.value().subList(1, list.value().size()));
    }
}
