package com.tomakeitgo.lisp.operators.control;

import com.tomakeitgo.lisp.Interpreter;
import com.tomakeitgo.lisp.SContext;
import com.tomakeitgo.lisp.SExpression;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static com.tomakeitgo.Context.parseAll;

public class ImportOperator implements SExpression.Operator {

    @Override
    public SExpression eval(List<SExpression> rest, Interpreter interpreter, SContext definitions) {
        if (rest.size() > 2) {
            return new SError("import requires a text argument and a atom");
        }
        SAtom module = null;
        if (rest.size() == 2 && rest.get(1) instanceof SAtom a) {
            module = a;
        }
        if (rest.getFirst() instanceof SText t) {
            SContext active = interpreter.createSContext();

            try {
                String command = new String(Files.readAllBytes(Path.of(System.getProperty("user.dir"), t.value())));
                for (SExpression expression : parseAll(command)) {
                    interpreter.eval(expression, active);
                }
                if (module == null) {
                    module = (SAtom) active.find(new SAtom(":module:name"));
                }
                SList exports = (SList) active.find(new SAtom(":module:exports"));
                
                for (SExpression expression : exports.value()) {
                    if(expression instanceof SAtom symbol) {
                        String outputName = module.value() + "/" + symbol.value();
                        definitions.set(new SAtom(outputName), active.find(symbol));
                    }
                }

                return new SText("loaded");
            } catch (IOException e) {
                return new SError("import failed!!!");
            }
        } else {
            return new SError("import requires a text argument and a atom");
        }
    }
}
