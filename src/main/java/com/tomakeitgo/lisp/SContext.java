package com.tomakeitgo.lisp;

import java.util.HashMap;
import java.util.function.Function;

public class SContext {
    private final HashMap<SExpression, SExpression> values = new HashMap<>();

    
    public void register(String atom, SExpression definition){
        this.values.put(new SExpression.SAtom(atom), definition);
    }
    
    public void register(SExpression expression, SExpression definition) {
        this.values.put(expression, definition);
    }
    
    public SExpression find(SExpression expression) {
        return values.getOrDefault(expression, expression);
    }

    public SContext copy() {
        SContext def = new SContext();
        def.values.putAll(this.values);
        return def;
    }
}
