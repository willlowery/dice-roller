package com.tomakeitgo.lisp.operators.list;

import com.tomakeitgo.lisp.Interpreter;
import com.tomakeitgo.lisp.SContext;
import com.tomakeitgo.lisp.SExpression;

import java.util.List;

public class ListContainsOperator extends SExpression.Lambda {
    @Override
    public SExpression eval(List<SExpression> rest, Interpreter interpreter, SContext definitions) {
        if (rest.size() != 2) {
            return new SError("list/contains requires exactly two arguments");
        }

        if (!(rest.get(0) instanceof SExpression.SList list)) {
            return new SError("list/contains requires a list as the first argument");
        }

        SExpression target = rest.get(1);

        if (list.value().contains(target)) {
            return Interpreter.TRUE;
        }
        return Interpreter.FALSE;
    }
}
