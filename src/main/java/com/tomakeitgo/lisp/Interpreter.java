package com.tomakeitgo.lisp;

import com.tomakeitgo.lisp.operators.*;

import java.util.List;

public class Interpreter {
    public static final SExpression.SAtom TRUE = new SExpression.SAtom("true");
    public static final SExpression.SAtom FALSE = new SExpression.SAtom("false");

    public static SContext createSContext() {
        SContext sContext = new SContext();
        sContext.register("if", new IfOperator());
        sContext.register("isEqual", new IsEqualOperator());

        sContext.register("roll", new RollOperator());

        sContext.register("type/isList", new IsTypeOperator(SExpression.SList.class));
        sContext.register("type/isText", new IsTypeOperator(SExpression.SText.class));
        sContext.register("type/isAtom", new IsTypeOperator(SExpression.SAtom.class));
        sContext.register("type/isNumber", new IsTypeOperator(SExpression.SNumber.class));
        sContext.register("type/isError", new IsTypeOperator(SExpression.Error.class));
        sContext.register("type/isLambda", new IsTypeOperator(SExpression.Lambda.class));

        sContext.register("text/concat", new TextConcatOperator());
        sContext.register("number/add", new AddNumberOperator());
        sContext.register("number/sub", new SubNumberOperator());
        sContext.register("number/mul", new MulNumberOperator());
        sContext.register("number/div", new DivNumberOperator());

        sContext.register("help", new HelpOperator());

        return sContext;
    }

    public SExpression eval(
            SExpression expression,
            SContext definitions
    ) {
        return switch (expression) {
            case SExpression.Error e -> e;
            case SExpression.SNumber t -> t;
            case SExpression.SText t -> t;
            case SExpression.Lambda l -> l;
            case SExpression.SAtom t -> definitions.find(t);
            case SExpression.SList l -> {
                var list = l.value();
                if (list.isEmpty()) yield l;

                var rest = list.subList(1, list.size());
                var key = definitions.find(list.getFirst());

                if (key.equals(new SExpression.SAtom("def"))) {
                    definitions.register(rest.get(0), rest.get(1));
                    yield rest.get(0);
                } else if (key.equals(new SExpression.SAtom("lambda"))) {
                    yield new SExpression.Lambda(rest, definitions);
                } else if (key instanceof SExpression.SList keyList) {
                    key = eval(keyList, definitions);
                }
                if (key instanceof IfOperator ifOperator) {
                    yield ifOperator.eval(rest, this, definitions);
                } else if (key instanceof SExpression.Lambda lambda) {
                    List<SExpression> args = rest.stream().map(i -> eval(i, definitions)).toList();
                    yield lambda.eval(args, this, definitions);
                }

                yield l;
            }
        };
    }
}
