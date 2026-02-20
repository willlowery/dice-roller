package com.tomakeitgo.lisp.operators.text;

import com.tomakeitgo.lisp.Interpreter;
import com.tomakeitgo.lisp.SContext;
import com.tomakeitgo.lisp.SExpression;

import java.util.List;

public class TextStartsWithOperator implements SExpression.Operator {
    @Override
    public SExpression eval(List<SExpression> rest, Interpreter interpreter, SContext definitions) {
        if (rest.size() != 2) {
            return new SError("text/startsWith requires exactly two arguments of type text");
        }

        if (!(rest.get(0) instanceof SText first)) {
            return new SError("text/startsWith requires exactly two arguments of type text");
        }

        if (!(rest.get(1) instanceof SText second)) {
            return new SError("text/startsWith requires exactly two arguments of type text");
        }

        return first.value().startsWith(second.value()) ? Interpreter.TRUE : Interpreter.FALSE;
    }
}
