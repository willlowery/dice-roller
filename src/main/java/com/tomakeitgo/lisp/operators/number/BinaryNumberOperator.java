package com.tomakeitgo.lisp.operators.number;

import com.tomakeitgo.lisp.Interpreter;
import com.tomakeitgo.lisp.SContext;
import com.tomakeitgo.lisp.SExpression;

import java.util.List;
import java.util.function.BiFunction;

public class BinaryNumberOperator implements SExpression.Operator {
    private final BiFunction<SExpression.SNumber, SExpression.SNumber, SExpression> operation;

    public BinaryNumberOperator(BiFunction<SExpression.SNumber, SExpression.SNumber, SExpression> operation) {
        this.operation = operation;
    }

    @Override
    public SExpression eval(List<SExpression> rest, Interpreter interpreter, SContext definitions) {
        if (rest.size() == 2) {
            if (rest.get(0) instanceof SExpression.SNumber first && rest.get(1) instanceof SExpression.SNumber second) {
                return operation.apply(first, second);
            } else {
                return new SError("requires two arguments of type Number");
            }
        } else {
            return new SError("requires two arguments of type Number");
        }
    }
}
