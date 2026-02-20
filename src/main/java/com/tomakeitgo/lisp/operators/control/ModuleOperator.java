package com.tomakeitgo.lisp.operators.control;

import com.tomakeitgo.lisp.Interpreter;
import com.tomakeitgo.lisp.SContext;
import com.tomakeitgo.lisp.SExpression;

import java.util.List;

public class ModuleOperator implements SExpression.Operator {
    @Override
    public SExpression eval(List<SExpression> rest, Interpreter interpreter, SContext definitions) {
        if (rest.size() != 2) {
            return new SError("module requires two arguments. module name and exported tokens");
        }
        if (!(rest.get(0) instanceof SAtom)) {
            return new SError("module requires a module name");
        }
        if (!(rest.get(1) instanceof SList)) {
            return new SError("module requires a export list");
        }

        definitions.set(new SAtom(":module:name"), rest.get(0));
        definitions.set(new SAtom(":module:exports"), rest.get(1));

        return rest.get(0);
    }
}
