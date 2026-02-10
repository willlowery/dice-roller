package com.tomakeitgo.lisp.operators;

import com.tomakeitgo.lisp.Interpreter;
import com.tomakeitgo.lisp.SContext;
import com.tomakeitgo.lisp.SExpression;

import java.util.List;

public class HelpOperator extends SExpression.Lambda {
    @Override
    public SExpression eval(List<SExpression> rest, Interpreter interpreter, SContext definitions) {
        String help = String.join("\n",
                "Available operations:",
                "",
                "  eval         (eval ...)                     evaluate the given expressions and returns the last one",
                "  if           (if condition then else)       conditional evaluation",
                "  isEqual      (isEqual a b ...)              equality check",
                "  roll         (roll '2d6')                   dice roll",
                "  type/isList  (type/isList x)                check if value is a list",
                "  type/isText  (type/isText x)                check if value is text",
                "  type/isAtom  (type/isAtom x)                check if value is an atom",
                "  type/isNumber (type/isNumber x)             check if value is a number",
                "  type/isError (type/isError x)               check if value is an error",
                "  type/isLambda (type/isLambda x)             check if value is a lambda",
                "  text/concat  (text/concat a b ...)          string concatenation",
                "  number/add   (number/add a b)               addition",
                "  number/sub   (number/sub a b)               subtraction",
                "  number/mul   (number/mul a b)               multiplication",
                "  number/div   (number/div a b)               division",
                "  number/mod   (number/mod a b)               remainder",
                "  number/divInt (number/divInt a b)           integer division",
                "  list/append  (list/append list items...)    append items to a list",
                "  list/isEmpty (list/isEmpty x)               check if list is empty",
                "  list/first  (list/first x)                  get first item of a list",
                "  list/rest   (list/rest x)                   get all items except the first",
                "  def          (def name value)               define a binding",
                "  lambda       (lambda (args) body)           create a function",
                "  help         (help)                         show this help"
        );
        return new SExpression.SText(help);
    }
}
