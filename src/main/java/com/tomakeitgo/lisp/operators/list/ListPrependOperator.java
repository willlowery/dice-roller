package com.tomakeitgo.lisp.operators.list;

import com.tomakeitgo.lisp.Interpreter;
import com.tomakeitgo.lisp.SContext;
import com.tomakeitgo.lisp.SExpression;

import java.util.ArrayList;
import java.util.List;

public class ListPrependOperator implements SExpression.Operator {
    @Override
    public SExpression eval(List<SExpression> rest, Interpreter interpreter, SContext definitions) {
        if (rest.size() < 2) {
            return new SError("requires a list and at least one item to prepend");
        }
        if (!(rest.getFirst() instanceof SList list)) {
            return new SError("first argument must be a list");
        }
        var result = new ArrayList<SExpression>();
        result.addAll(rest.subList(1, rest.size()));
        result.addAll(list.value());
        return new SList(result);
    }
}
