package com.tomakeitgo.dice;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Grammar
 * <ul>
 *     <li>expr :=  value | operator value+ expr</li>
 *     <li>value := dice | constant</li>
 *     <li>dice := DIGITS D DIGITS</li>
 *     <li>constant := DIGITS</li>
 *     <li>operator := MINUS | PLUS</li>
 * </ul>
 * <p>
 * Lexicon
 * <ul>
 * <li>D := [dD]</li>
 * <li>DIGITS := \d+</li>
 * <li>PLUS := [+]</li>
 * <li>MINUS := [-]</li>
 * </ul>
 */
public class Parser {

    public DiceExpression parse(List<Lexer.Token> tokens) {
        if (!tokens.isEmpty()) {
            if (tokens.getFirst().getType().equals(Lexer.Token.Type.PLUS)) {
                V v = parseValue(tokens.subList(1, tokens.size()));
                List<DiceExpression> expression = new ArrayList<>(v.expression());
                expression.add(parse(v.rest()));
                return new BinaryOperator("+", Integer::sum, expression);
            } else if (tokens.getFirst().getType().equals(Lexer.Token.Type.MINUS)) {
                V v = parseValue(tokens.subList(1, tokens.size()));
                List<DiceExpression> expression = new ArrayList<>(v.expression());
                expression.add(parse(v.rest()));
                return new BinaryOperator("-", (a, b) -> a - b, expression);
            } else {
                return parseValue(tokens).expression().getFirst();
            }
        }

        return (r) -> new DiceExpression.Result(true, "", 0);
    }

    public V parseValue(List<Lexer.Token> tokens) {

        if (tokens.size() >= 3) {
            if (!tokens.getFirst().getType().equals(Lexer.Token.Type.DIGITS)) {
                return new V(List.of(), tokens);

            } else if (tokens.get(1).getType().equals(Lexer.Token.Type.D)) {
                var rest = tokens.subList(3, tokens.size());
                return new V(List.of(parseDice(tokens.subList(0, 3))), rest);
            } else {
                var rest = tokens.subList(1, tokens.size());

                var remaining = parseValue(rest);
                var items = new ArrayList<DiceExpression>();
                items.add(parseConstant(tokens.getFirst()));
                items.addAll(remaining.expression());
                return new V(items, remaining.rest());
            }
        } else if (tokens.getFirst().getType().equals(Lexer.Token.Type.DIGITS)) {
            return new V(List.of(parseConstant(tokens.getFirst())), tokens.subList(1, tokens.size()));
        } else {
            return new V(List.of(), tokens);
        }
    }

    private record V(List<DiceExpression> expression, List<Lexer.Token> rest) {
    }

    public DiceExpression parseConstant(Lexer.Token token) {
        return (r) -> new DiceExpression.Result(true, token.getValue(), Integer.parseInt(token.getValue()));
    }

    public DiceExpression parseDice(List<Lexer.Token> tokens) {
        return (r) -> {
            var numberOfDice = Integer.parseInt(tokens.get(0).getValue());
            var numberOfSides = Integer.parseInt(tokens.get(2).getValue());

            int total = 0;
            for (int i = 0; i < numberOfDice; i++) {
                total += r.random(numberOfSides);
            }
            return new DiceExpression.Result(true, numberOfDice + "D" + numberOfSides, total);
        };
    }

    public static class BinaryOperator implements DiceExpression {

        private final String symbol;
        private final java.util.function.BinaryOperator<Integer> operation;
        private final List<DiceExpression> items;

        public BinaryOperator(String symbol, java.util.function.BinaryOperator<Integer> operation, List<DiceExpression> items) {
            this.symbol = symbol;
            this.operation = operation;
            this.items = items;
        }

        @Override
        public Result eval(Rand rand) {
            var results = items.stream().map(i -> i.eval(rand)).toList();
            var description = results.stream().map(Result::describe).collect(Collectors.joining(" ")).trim();
            return new Result(true, symbol + " " + description, results.stream().map(Result::getValue).reduce(0, operation));
        }
    }


    public interface DiceExpression {

        Result eval(Rand rand);

        class Result {
            private String description;
            private boolean valid;
            private final int value;

            public Result(boolean valid, String description, int value) {
                this.description = description;
                this.valid = valid;
                this.value = value;
            }

            public boolean valid() {
                return valid;
            }

            public String describe() {
                return description;
            }

            public int getValue() {
                return value;
            }
        }

        interface Rand {
            /**
             * Returns a number between 1 and max
             */
            int random(int max);
        }
    }
}
