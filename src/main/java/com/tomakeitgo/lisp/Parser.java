package com.tomakeitgo.lisp;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Parser {

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
