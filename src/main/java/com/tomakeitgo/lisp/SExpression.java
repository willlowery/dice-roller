package com.tomakeitgo.lisp;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public sealed interface SExpression {
    record SList(List<SExpression> value) implements SExpression {
        @Override
        public String toString() {
            return "(" + value.stream().map(Objects::toString).collect(Collectors.joining(" ")) +")";
        }
    }

    record SAtom(String value) implements SExpression {
        @Override
        public String toString() {
            return value;
        }
    }

    record SText(String value) implements SExpression {
        @Override
        public String toString() {
            return "'" + value.replace("'", "''") + "'";
        }
    }

    record SError(String message) implements SExpression {
        @Override
        public String toString() {
            return "Error: "+ message;
        }
    }
    
    record SNumber(BigDecimal value) implements SExpression {
        public SNumber(int value) {
            this(new BigDecimal(value));    
        }

        public SNumber add(SNumber value) {
            return new SNumber(this.value.add(value.value(), MathContext.DECIMAL128));
        }

        public SNumber sub(SNumber value) {
            return new SNumber(this.value.subtract(value.value(), MathContext.DECIMAL128));
        }

        public SExpression mul(SNumber value) {
            return new SNumber(this.value.multiply(value.value(), MathContext.DECIMAL128));
        }

        public SExpression div(SNumber value) {
            return new SNumber(this.value.divide(value.value(), MathContext.DECIMAL128));
        }

        public SExpression mod(SNumber value) {
            return new SNumber(this.value.remainder(value.value(), MathContext.DECIMAL128));
        }

        public SExpression divInt(SNumber value) {
            return new SNumber(this.value.divideToIntegralValue(value.value(), MathContext.DECIMAL128));
        }
        
        public SExpression lt(SNumber value) {
            return value().compareTo(value.value()) < 0 ? Interpreter.TRUE : Interpreter.FALSE;
        }
        
        public SExpression lte(SNumber value) {
            return value().compareTo(value.value()) <= 0 ? Interpreter.TRUE : Interpreter.FALSE;
        }
        
        public SExpression gt(SNumber value) {
            return value().compareTo(value.value()) > 0 ? Interpreter.TRUE : Interpreter.FALSE;
        }
        
        public SExpression gte(SNumber value) {
            return value().compareTo(value.value()) >= 0 ? Interpreter.TRUE : Interpreter.FALSE;
        }

        static SNumber from(String value) {
            return new SNumber(new BigDecimal(value));
        }

        @Override
        public String toString() {
            return value.stripTrailingZeros().toPlainString();
        }
    }

    non-sealed class Lambda implements SExpression {
        private final SList arguments;
        private final List<SExpression> expression;
        private final SContext context;

        public Lambda() {
            arguments = null;
            expression = null;
            context = null;
        }

        public Lambda(List<SExpression> remaining, SContext context) {
            this.arguments = (SList) remaining.getFirst();
            this.expression = remaining;
            this.context = context.copy();
        }

        public SExpression eval(List<SExpression> rest, Interpreter interpreter, SContext definitions) {
            if (arguments.value().size() != rest.size()) return new SError("Invalid number of arguments");
            for (int i = 0; i < arguments.value().size(); i++) {
                var arg = arguments.value().get(i);
                var value = rest.get(i);
                context.register(arg, value);
            }

            SExpression result = new SError("Lambda body needs at least one statement");
            for (SExpression expression : expression) {
                result = interpreter.eval(expression, context);
            }
            return result;
        }

        @Override
        public String toString() {
            return "Lambda{" +
                    "arguments=" + arguments +
                    ", expression=" + expression +
                    '}';
        }
    }
}
