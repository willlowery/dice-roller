package com.tomakeitgo.lisp;

import com.tomakeitgo.lisp.SExpression.SAtom;
import com.tomakeitgo.lisp.operators.*;
import com.tomakeitgo.lisp.operators.control.*;
import com.tomakeitgo.lisp.operators.dice.*;
import com.tomakeitgo.lisp.operators.list.*;
import com.tomakeitgo.lisp.operators.number.*;
import com.tomakeitgo.lisp.operators.system.*;
import com.tomakeitgo.lisp.operators.text.*;
import com.tomakeitgo.lisp.operators.type.*;

import java.util.List;

import static com.tomakeitgo.lisp.SExpression.*;

public class Interpreter {
    public static final SAtom TRUE = new SAtom("true");
    public static final SAtom FALSE = new SAtom("false");

    public static SContext createSContext() {
        SContext sContext = new SContext();
        sContext.register("eval", new EvalOperator());

        sContext.register("if", new IfOperator());
        sContext.register("isEqual", new IsEqualOperator());

        sContext.register("roll", new RollOperator());

        sContext.register("type/isList", new IsTypeOperator(SList.class));
        sContext.register("type/isText", new IsTypeOperator(SText.class));
        sContext.register("type/isAtom", new IsTypeOperator(SAtom.class));
        sContext.register("type/isNumber", new IsTypeOperator(SNumber.class));
        sContext.register("type/isError", new IsTypeOperator(Error.class));
        sContext.register("type/isLambda", new IsTypeOperator(Lambda.class));
        
        sContext.register("text/concat", new TextConcatOperator());
        
        sContext.register("number/add", new BinaryNumberOperator(SNumber::add));
        sContext.register("number/sub", new BinaryNumberOperator(SNumber::sub));
        sContext.register("number/mul", new BinaryNumberOperator(SNumber::mul));
        sContext.register("number/div", new BinaryNumberOperator(SNumber::div));
        sContext.register("number/mod", new BinaryNumberOperator(SNumber::mod));
        sContext.register("number/divInt", new BinaryNumberOperator(SNumber::divInt));
        
        sContext.register("list/append", new ListAppendOperator());
        sContext.register("list/isEmpty", new IsEmptyOperator());
        sContext.register("list/first", new ListFirstOperator());
        sContext.register("list/rest", new ListRestOperator());
        
        sContext.register("help", new HelpOperator());

        return sContext;
    }

    public SExpression eval(
            SExpression expression,
            SContext definitions
    ) {
        return switch (expression) {
            case SExpression.Error e -> e;
            case SNumber t -> t;
            case SText t -> t;
            case Lambda l -> l;
            case SAtom t -> definitions.find(t);
            case SList l -> {
                var list = l.value();
                if (list.isEmpty()) yield l;

                var rest = list.subList(1, list.size());
                var key = definitions.find(list.getFirst());

                if (key.equals(new SAtom("def"))) {
                    definitions.register(rest.get(0), rest.get(1));
                    yield rest.get(0);
                } else if (key.equals(new SAtom("lambda"))) {
                    yield new Lambda(rest, definitions);
                } else if (key instanceof SList keyList) {
                    key = eval(keyList, definitions);
                }
                if (key instanceof IfOperator ifOperator) {
                    yield ifOperator.eval(rest, this, definitions);
                } else if (key instanceof Lambda lambda) {
                    List<SExpression> args = rest.stream().map(i -> eval(i, definitions)).toList();
                    yield lambda.eval(args, this, definitions);
                }

                yield l;
            }
        };
    }
}
