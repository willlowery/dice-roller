package com.tomakeitgo.lisp.operators.text;

import com.tomakeitgo.lisp.Interpreter;
import com.tomakeitgo.lisp.SContext;
import com.tomakeitgo.lisp.SExpression;

import java.util.List;

public class TextConcatOperator extends SExpression.Lambda {
    @Override
    public SExpression eval(List<SExpression> rest, Interpreter interpreter, SContext definitions) {
        StringBuilder value = new StringBuilder();
        for (SExpression sExpression : rest) {
            if (interpreter.eval(sExpression, definitions) instanceof SExpression.SText text) {
                value.append(text.value());
            }
        }
        return new SExpression.SText(value.toString());
    }
}
