package com.tomakeitgo.lisp;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.tomakeitgo.lisp.Lexer.Token.Type.*;

/**
 * expr: ATOM
 * | STRING
 * | NUMBER
 * | PAREN_OPEN expr* PAREN_CLOSE
 * ----
 * PAREN_OPEN: [(]  ;
 * PAREN_CLOSE: [)] ;
 * NUMBER: -?[0-9]+(.[0-9]*)? ;
 * STRING '.*' ;
 * SPACE: \s+  ;
 * ATOM: \S+   ;
 */
public class Lexer {

    public List<Token> lex(String input) {
        ArrayList<Token> tokens = new ArrayList<>();
        for (int i = 0; i < input.length(); ) {
            if (input.charAt(i) == '(') {
                tokens.add(new Token(PAREN_OPEN, "("));
                i++;
            } else if (input.charAt(i) == ')') {
                tokens.add(new Token(PAREN_CLOSE, ")"));
                i++;
            } else if (input.charAt(i) == '\'') {
                StringBuilder value = new StringBuilder();
                i++;
                while (i < input.length()) {
                    char c = input.charAt(i);
                    if (c == '\'') {
                        if (i + 1 < input.length() && input.charAt(i + 1) == '\'') {
                            value.append("'");
                            i++;
                        } else {
                            i++;
                            break;
                        }
                    } else {
                        value.append(c);
                    }
                    i++;
                }
                tokens.add(new Token(STRING, value.toString()));
            } else if (i + 1 < input.length() && input.charAt(i) == '-' && Character.isDigit(input.charAt(i + 1))) {

                StringBuilder value = new StringBuilder();
                value.append("-");
                i++;

                while (i < input.length()) {
                    if (!Character.isDigit(input.charAt(i))) {
                        break;
                    }
                    value.append(input.charAt(i));
                    i++;
                }
                if (i + 1 < input.length() && '.' == input.charAt(i) && Character.isDigit(input.charAt(i + 1))) {
                    value.append('.');
                    i++;

                    while (i < input.length()) {
                        if (!Character.isDigit(input.charAt(i))) {
                            break;
                        }
                        value.append(input.charAt(i));
                        i++;
                    }
                }
                tokens.add(new Token(Token.Type.NUMBER, value.toString()));
            } else if (
                    Character.isDigit(input.charAt(i))
            ) {

                StringBuilder value = new StringBuilder();
                while (i < input.length()) {
                    if (!Character.isDigit(input.charAt(i))) {
                        break;
                    }
                    value.append(input.charAt(i));
                    i++;
                }
                if (i + 1 < input.length() && '.' == input.charAt(i) && Character.isDigit(input.charAt(i + 1))) {
                    value.append('.');
                    i++;

                    while (i < input.length()) {
                        if (!Character.isDigit(input.charAt(i))) {
                            break;
                        }
                        value.append(input.charAt(i));
                        i++;
                    }
                }
                
                tokens.add(new Token(Token.Type.NUMBER, value.toString()));
            } else if (Character.isWhitespace(input.charAt(i))) {
                while (i < input.length() && Character.isWhitespace(input.charAt(i))) {
                    i++;
                }
            } else {
                StringBuilder value = new StringBuilder();
                while (i < input.length() &&
                        !Character.isWhitespace(input.charAt(i)) &&
                        input.charAt(i) != '(' && input.charAt(i) != ')'
                ) {
                    value.append(input.charAt(i));
                    i++;
                }
                tokens.add(new Token(ATOM, value.toString()));
            }
        }
        return tokens;
    }

    public static class Token {
        Type type;
        String value;

        public Token(Type type, String value) {
            this.type = type;
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public Type getType() {
            return type;
        }

        public enum Type {
            PAREN_OPEN,
            PAREN_CLOSE,
            ATOM,
            NUMBER,
            STRING

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
