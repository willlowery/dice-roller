package com.tomakeitgo.lisp.operators.list;

import com.tomakeitgo.lisp.Interpreter;
import com.tomakeitgo.lisp.SContext;
import com.tomakeitgo.lisp.SExpression;

import java.util.ArrayList;
import java.util.List;

public class ListAppendOperator extends SExpression.Lambda {
    @Override
    public SExpression eval(List<SExpression> rest, Interpreter interpreter, SContext definitions) {
        if (rest.size() < 2) {
            return new SError("requires a list and at least one item to append");
        }
        if (!(rest.getFirst() instanceof SExpression.SList list)) {
            return new SError("first argument must be a list");
        }
        var result = new ArrayList<>(list.value());
        result.addAll(rest.subList(1, rest.size()));
        return new SExpression.SList(result);
    }
}
