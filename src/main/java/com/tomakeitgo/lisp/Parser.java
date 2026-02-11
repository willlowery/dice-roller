package com.tomakeitgo.lisp;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Parser {
    public List<SExpression> parseAll(List<Lexer.Token> expressions) {

        LinkedList<Lexer.Token> toProcess = new LinkedList<>(expressions);
        ArrayList<SExpression> list = new ArrayList<>();
        while (!toProcess.isEmpty()) {
            SExpression parse = parse(toProcess);
            if (parse == null) break;
            list.add(parse);
        }
        return list;
    }

    public SExpression parse(List<Lexer.Token> tokens) {
        if (tokens.isEmpty()) return null;
        return parse(new LinkedList<>(tokens));
    }


    private SExpression parse(LinkedList<Lexer.Token> toProcess) {
        if (toProcess.isEmpty()) return null;
        var item = toProcess.pop();
        if (item.getType().equals(Lexer.Token.Type.ATOM)) {
            return new SExpression.SAtom(item.getValue());
        } else if (item.getType().equals(Lexer.Token.Type.NUMBER)) {
            return SExpression.SNumber.from(item.getValue());
        } else if (item.getType().equals(Lexer.Token.Type.STRING)) {
            return new SExpression.SText(item.getValue());
        } else if (item.getType().equals(Lexer.Token.Type.PAREN_OPEN)) {
            ArrayList<SExpression> list = new ArrayList<>();
            SExpression a;
            while ((a = parse(toProcess)) != null) {
                list.add(a);
            }
            return new SExpression.SList(list);
        } else if (item.getType().equals(Lexer.Token.Type.PAREN_CLOSE)) {
            return null;
        }
        return null;
    }

}
