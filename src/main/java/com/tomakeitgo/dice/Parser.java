package com.tomakeitgo.dice;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
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
        if (tokens.isEmpty()) {
            return (r) -> new DiceExpression.Result("", true, 0, 0, 0);
        }
        return parse(new LinkedList<>(tokens));
    }

    private DiceExpression parse(LinkedList<Lexer.Token> tokens) {
        if (tokens.getFirst().getType().equals(Lexer.Token.Type.PLUS)) {
            tokens.pop();
            List<DiceExpression> expression = new ArrayList<>();
            DiceExpression value;
            while ((value = parseValue(tokens)) != null) {
                expression.add(value);
            }
            if (!tokens.isEmpty()) {
                expression.add(parse(tokens));
            }
            return new BinaryOperator("+", (a, b) -> a + b, expression);
        } else if (tokens.getFirst().getType().equals(Lexer.Token.Type.MINUS)) {
            tokens.pop();
            List<DiceExpression> expression = new ArrayList<>();
            DiceExpression value;
            while ((value = parseValue(tokens)) != null) {
                expression.add(value);
            }
            if (!tokens.isEmpty()) {
                expression.add(parse(tokens));
            }
            return new BinaryOperator("-", (a, b) -> a - b, expression);
        } else {
            return parseValue(tokens);
        }

    }

    private DiceExpression parseValue(LinkedList<Lexer.Token> tokens) {
        var die = parseDie(tokens);
        if (die == null) {
            return parseNumber(tokens);
        } else {
            return die;
        }
    }

    private DiceExpression parseNumber(LinkedList<Lexer.Token> tokens) {
        if (tokens.isEmpty()) return null;
        if (!tokens.getFirst().getType().equals(Lexer.Token.Type.DIGITS)) return null;
        var number = tokens.pop();
        return (r) -> new DiceExpression.Result(
                number.getValue(),
                true,
                Integer.parseInt(number.getValue()),
                Integer.parseInt(number.getValue()),
                Integer.parseInt(number.getValue())
        );
    }

    private DiceExpression parseDie(LinkedList<Lexer.Token> tokens) {
        if (tokens.size() < 3) return null;

        if (!tokens.get(0).getType().equals(Lexer.Token.Type.DIGITS)) {
            return null;
        }
        if (!tokens.get(1).getType().equals(Lexer.Token.Type.D)) {
            return null;
        }
        if (!tokens.get(2).getType().equals(Lexer.Token.Type.DIGITS)) {
            return null;
        }
        var count = tokens.pop();
        var d = tokens.pop();
        var sides = tokens.pop();

        return (r) -> {
            var numberOfDice = Integer.parseInt(count.getValue());
            var numberOfSides = Integer.parseInt(sides.getValue());

            int total = 0;
            for (int i = 0; i < numberOfDice; i++) {
                total += r.random(numberOfSides);
            }
            return new DiceExpression.Result(
                    numberOfDice + "D" + numberOfSides + "(" + total + ")",
                    true,
                    total,
                    numberOfDice,
                    numberOfDice * numberOfSides
            );
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
            var description = results.stream().map(Result::description).collect(Collectors.joining(" ")).trim();

            int max = 0;
            int min = 0;
            int value = 0;
            if (results.size() > 1) {
                max = results.getFirst().max();
                min = results.getFirst().min();
                value = results.getFirst().value();
            }
            for (int i = 1; i < results.size(); i++) {
                Result result = results.get(i);
                max = operation.apply(max, result.max());
                min = operation.apply(min, result.min());
                value = operation.apply(value, result.value());
            }


            return new Result(symbol + " " + description,
                    true,
                    value,
                    min,
                    max
            );
        }
    }


    public interface DiceExpression {

        Result eval(Rand rand);

        record Result(String description, boolean valid, int value, int min, int max) {

        }

        interface Rand {
            /**
             * Returns a number between 1 and max
             */
            int random(int max);
        }
    }
}
