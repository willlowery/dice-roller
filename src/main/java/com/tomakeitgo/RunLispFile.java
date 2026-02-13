package com.tomakeitgo;

import com.tomakeitgo.lisp.Interpreter;
import com.tomakeitgo.lisp.SExpression;

import java.nio.file.Files;
import java.nio.file.Path;

import static com.tomakeitgo.Context.parseAll;

public class RunLispFile {
    public static void main(String[] args) throws Exception {
        var active = Interpreter.createSContext();
        
        String command = new String(Files.readAllBytes(Path.of(System.getProperty("user.dir"), "test.lsp")));
        for (SExpression expression : parseAll(command)) {
            SExpression eval = new Interpreter().eval(expression, active);
            System.out.println(eval);
        }
    }
}
