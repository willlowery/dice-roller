package com.tomakeitgo.dice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Lexer {
    public List<Token> lex(String input) {
        if (input == null || input.isBlank()) return Collections.emptyList();

        ArrayList<Token> tokens = new ArrayList<>();

        for (int i = 0; i < input.length(); ) {
            char c = input.charAt(i);
            if (c == 'd' || c == 'D') {
                tokens.add(new Token(Token.Type.D, String.valueOf(c).toUpperCase()));
                i++;
            } else if (c == '-') {
                tokens.add(new Token(Token.Type.MINUS, String.valueOf(c).toUpperCase()));
                i++;
            } else if (c == '+') {
                tokens.add(new Token(Token.Type.PLUS, String.valueOf(c).toUpperCase()));
                i++;
            } else if (Character.isDigit(c)) {
                StringBuilder buffer = new StringBuilder();
                buffer.append(c);
                i++;

                while (i < input.length() && Character.isDigit(input.charAt(i))) {
                    c = input.charAt(i);
                    buffer.append(c);
                    i++;
                }
               

                tokens.add(new Token(Token.Type.DIGITS, buffer.toString()));
            } else {
                // do nothing yet
                //Mystery Tokens! We should probably yell...
                i++;
            }


        }
        return tokens;
    }

    public static class Token {
        private final Type type;
        private final String value;

        public Token(Type type, String value) {
            this.type = type;
            this.value = value;
        }

        public enum Type {
            D,
            DIGITS,
            PLUS,
            MINUS
        }

        public String getValue() {
            return value;
        }

        public Type getType() {
            return type;
        }

        @Override
        public String toString() {
            return "Token{" +
                    "type=" + type +
                    ", value='" + value + '\'' +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Token token = (Token) o;
            return type == token.type && Objects.equals(value, token.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(type, value);
        }
    }
}
