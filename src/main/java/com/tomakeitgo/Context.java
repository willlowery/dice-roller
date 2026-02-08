package com.tomakeitgo;

import com.tomakeitgo.lisp.Interpreter;
import com.tomakeitgo.lisp.SContext;
import com.tomakeitgo.lisp.SExpression;

import java.util.ArrayList;
import java.util.List;

public class Context {
    private boolean running = true;
    private final ArrayList<String> commands = new ArrayList<>();
    private final ArrayList<String> commandOutput = new ArrayList<>();
    private int active;
    private final List<Object> availableActive = new ArrayList<>();
    private final SContext SContext = Interpreter.createSContext();

    public Context() {
        SContext.register(new SExpression.SAtom("quit"), new SExpression.Lambda() {
                    @Override
                    public SExpression eval(List<SExpression> rest, Interpreter interpreter, com.tomakeitgo.lisp.SContext definitions) {
                        shutdown();
                        return new SText("Stopping!");
                    }
                }
        );
        SContext.register(new SExpression.SAtom("clear"),
                new SExpression.Lambda() {
                    @Override
                    public SExpression eval(List<SExpression> rest, Interpreter interpreter, com.tomakeitgo.lisp.SContext definitions) {
                        commandOutput.clear();
                        return null;
                    }
                }
        );
        

    }

    public void shutdown() {
        running = false;
    }

    public boolean isRunning() {
        return running;
    }

    public void queue(String command) {
        commands.add(command);
        SExpression exp = new com.tomakeitgo.lisp.Parser().parse(new com.tomakeitgo.lisp.Lexer().lex(command));

        var a = new Interpreter().eval(exp, SContext);
        if (a != null)
            commandOutput.add(a.toString());
    }

    public ArrayList<String> getCommands() {
        return commands;
    }

    public ArrayList<String> getCommandOutput() {
        return commandOutput;
    }

    public void setActive(Object active) {
        this.active = availableActive.indexOf(active);
    }

    public void addAvailableActive(Object active) {
        this.availableActive.add(active);
    }

    public boolean isActive(Object o) {
        return availableActive.indexOf(o) == active;
    }

    public void tabActive() {
        this.active = (this.active + 1) % availableActive.size();
    }
}
