package com.tomakeitgo.lisp;

import java.util.HashMap;

public class SContext {
    private final SContext parent;
    private final HashMap<SExpression, SExpression> values = new HashMap<>();

    public SContext(SContext parent) {
        this.parent = parent;
    }

    public void register(String atom, SExpression definition) {
        this.values.put(new SExpression.SAtom(atom), definition);
    }

    public void register(SExpression expression, SExpression definition) {
        this.values.put(expression, definition);
    }

    private SContext findContextContainingKey(SExpression expression) {
        if (values.containsKey(expression)) {
            return this;
        } else if (parent == null) {
            return null;
        } else {
            return parent.findContextContainingKey(expression);
        }
    }

    public void set(SExpression expression, SExpression definition) {
        var context = findContextContainingKey(expression);
        if (context != null) {
            context.values.put(expression, definition);
        } else {
            values.put(expression, definition);
        }
    }

    public SExpression find(SExpression expression) {
        var currentValue = values.get(expression);
        if (currentValue != null) {
            return currentValue;
        } else if (parent != null) {
            return parent.find(expression);
        } else {
            return expression;
        }
    }

    public SContext copy() {
        return new SContext(this);
    }
}
