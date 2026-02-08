package com.tomakeitgo.lisp;

import com.tomakeitgo.dice.Lexer;

import java.util.List;
import java.util.Random;

public class Interpreter {
    private static final Random random = new Random();
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

        sContext.register("text/concat", new TextConcat());
        sContext.register("number/add", new AddNumberOperator());
        sContext.register("number/sub", new SubNumberOperator());

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

    private static class RollOperator extends SExpression.Lambda {
        @Override
        public SExpression eval(List<SExpression> rest, Interpreter interpreter, com.tomakeitgo.lisp.SContext definitions) {
            var rollText = ((SText) (rest).getFirst()).value();
            var e = new com.tomakeitgo.dice.Parser().parse(new Lexer().lex(rollText));
            var result = e.eval((side) -> random.nextInt(1, side + 1));
            return new SText(result.describe() + ": " + result.getValue());
        }
    }

    private static class TextConcat extends SExpression.Lambda {
        @Override
        public SExpression eval(
                List<SExpression> rest,
                Interpreter interpreter,
                SContext definitions
        ) {
            StringBuilder value = new StringBuilder();
            for (SExpression sExpression : rest) {
                if (interpreter.eval(sExpression, definitions) instanceof SText text) {
                    value.append(text.value());
                }
            }
            return new SText(value.toString());
        }
    }

    private static class IfOperator extends SExpression.Lambda {
        @Override
        public SExpression eval(List<SExpression> rest, Interpreter interpreter, SContext definitions) {
            if (rest.size() != 3) {
                return new Error("if requires three arguments");
            }
            if (TRUE.equals(interpreter.eval(rest.getFirst(), definitions))) {
                return interpreter.eval(rest.get(1), definitions);
            } else {
                return interpreter.eval(rest.get(2), definitions);
            }
        }
    }

    private static class IsEqualOperator extends SExpression.Lambda {
        @Override
        public SExpression eval(List<SExpression> rest, Interpreter interpreter, SContext definitions) {
            if (rest.size() < 2) return new Error("isEqual requires at least two arguments");

            SExpression s = rest.getFirst();
            for (SExpression sExpression : rest.subList(1, rest.size())) {
                if (!sExpression.equals(s)) {
                    return FALSE;
                }
            }
            return TRUE;
        }
    }

    private static class IsTypeOperator extends SExpression.Lambda {
        private final Class<? extends SExpression> type;

        private IsTypeOperator(Class<? extends SExpression> type) {
            this.type = type;
        }

        @Override
        public SExpression eval(List<SExpression> rest, Interpreter interpreter, SContext definitions) {
            if (rest.size() != 1) return new Error("requires one argument.");
            return type.isInstance(rest.getFirst()) ? TRUE : FALSE;

        }
    }

    private static class AddNumberOperator extends SExpression.Lambda {
        @Override
        public SExpression eval(List<SExpression> rest, Interpreter interpreter, SContext definitions) {
            if (rest.size() == 2) {
                if (rest.get(0) instanceof SExpression.SNumber first && rest.get(1) instanceof SExpression.SNumber second) {
                    return first.add(second);
                } else {
                    return new Error("requires two arguments of type Number");
                }
            } else {
                return new Error("requires two arguments of type Number");
            }
        }
    }

    private static class SubNumberOperator extends SExpression.Lambda {
        @Override
        public SExpression eval(List<SExpression> rest, Interpreter interpreter, SContext definitions) {
            if (rest.size() == 2) {
                if (rest.get(0) instanceof SExpression.SNumber first && rest.get(1) instanceof SExpression.SNumber second) {
                    return first.sub(second);
                } else {
                    return new Error("requires two arguments of type Number");
                }
            } else {
                return new Error("requires two arguments of type Number");
            }
        }
    }

    private static class MulNumberOperator extends SExpression.Lambda {
        @Override
        public SExpression eval(List<SExpression> rest, Interpreter interpreter, SContext definitions) {
            if (rest.size() == 2) {
                if (rest.get(0) instanceof SExpression.SNumber first && rest.get(1) instanceof SExpression.SNumber second) {
                    return first.mul(second);
                } else {
                    return new Error("requires two arguments of type Number");
                }
            } else {
                return new Error("requires two arguments of type Number");
            }
        }
    }

    private static class DivNumberOperator extends SExpression.Lambda {
        @Override
        public SExpression eval(List<SExpression> rest, Interpreter interpreter, SContext definitions) {
            if (rest.size() == 2) {
                if (rest.get(0) instanceof SExpression.SNumber first && rest.get(1) instanceof SExpression.SNumber second) {
                    return first.div(second);
                } else {
                    return new Error("requires two arguments of type Number");
                }
            } else {
                return new Error("requires two arguments of type Number");
            }
        }
    }
}
