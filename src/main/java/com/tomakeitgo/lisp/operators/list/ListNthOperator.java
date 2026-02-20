package com.tomakeitgo.lisp.operators.list;

import com.tomakeitgo.lisp.Interpreter;
import com.tomakeitgo.lisp.SContext;
import com.tomakeitgo.lisp.SExpression;

import java.util.List;

public class ListNthOperator implements SExpression.Operator {
    @Override
    public SExpression eval(List<SExpression> rest, Interpreter interpreter, SContext definitions) {
        if (rest.size() != 2) {
            return new SError("list/nth requires exactly two arguments");
        }

        if (!(rest.get(0) instanceof SExpression.SList list)) {
            return new SError("list/nth requires a list as the first argument");
        }

        if (!(rest.get(1) instanceof SExpression.SNumber number)) {
            return new SError("list/nth requires a number as the second argument");
        }

        int index = number.value().intValueExact();
        if (index < 0 || index >= list.value().size()) {
            return new SError("list/nth index out of bounds");
        }

        return list.value().get(index);
    }
}
