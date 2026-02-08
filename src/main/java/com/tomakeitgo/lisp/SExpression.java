package com.tomakeitgo.lisp;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.List;

public sealed interface SExpression {
    record SList(List<SExpression> value) implements SExpression {
    }

    record SAtom(String value) implements SExpression {
    }

    record SText(String value) implements SExpression {
    }

    record Error(String message) implements SExpression {
    }

    record SNumber(BigDecimal value) implements SExpression {

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

        static SNumber from(String value) {
            return new SNumber(new BigDecimal(value));
        }


    }

    non-sealed class Lambda implements SExpression {
        private final SList arguments;
        private final SExpression expression;
        private final SContext SContext;

        public Lambda() {
            arguments = null;
            expression = null;
            SContext = null;
        }

        public Lambda(List<SExpression> remaining, SContext SContext) {
            this.arguments = (SList) remaining.getFirst();
            this.expression = remaining.get(1);
            this.SContext = SContext.copy();
        }

        public SExpression eval(List<SExpression> rest, Interpreter interpreter, SContext definitions) {
            for (int i = 0; i < arguments.value().size(); i++) {
                var arg = arguments.value().get(i);
                var value = rest.get(i);
                SContext.register(arg, value);
            }

            return interpreter.eval(expression, SContext);
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
