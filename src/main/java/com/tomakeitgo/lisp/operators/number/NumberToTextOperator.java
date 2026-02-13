package com.tomakeitgo.lisp.operators.number;

import com.tomakeitgo.lisp.Interpreter;
import com.tomakeitgo.lisp.SContext;
import com.tomakeitgo.lisp.SExpression;

import java.util.List;

public class NumberToTextOperator extends SExpression.Lambda {
    @Override
    public SExpression eval(List<SExpression> rest, Interpreter interpreter, SContext definitions) {
        if (rest.size() != 1) {
            return new SExpression.Error("number/text requires exactly one argument");
        }

        SExpression arg = rest.getFirst();
        if (!(arg instanceof SExpression.SNumber number)) {
            return new SExpression.Error("number/text requires a number argument");
        }

        return new SExpression.SText(number.value().stripTrailingZeros().toPlainString());
    }
}
