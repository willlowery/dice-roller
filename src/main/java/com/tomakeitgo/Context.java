package com.tomakeitgo;

import com.tomakeitgo.lisp.Interpreter;
import com.tomakeitgo.lisp.Lexer;
import com.tomakeitgo.lisp.Parser;
import com.tomakeitgo.lisp.SContext;
import com.tomakeitgo.lisp.SExpression;

import java.util.ArrayList;
import java.util.List;

public class Context {
    private boolean running = true;
    private final ArrayList<String> commands = new ArrayList<>();
    private final ArrayList<String> consoleLog = new ArrayList<>();
    private final ArrayList<String> commandOutput = new ArrayList<>();
    private int activePane;
    private final List<Object> panes = new ArrayList<>();
    private final SContext SContext = Interpreter.createSContext();

    public Context() {
        SContext.register(new SExpression.SAtom("host/send"), new ContextCallBackOperator(this));
        new Interpreter().eval(parse("(def log (lambda (x) (host/send 'log' x)))"), SContext);
        new Interpreter().eval(parse("(def clear (lambda () (host/send 'clear')))"), SContext);
        new Interpreter().eval(parse("(def quit (lambda () (host/send 'quit')))"), SContext);
    }

    public void shutdown() {
        running = false;
    }

    public boolean isRunning() {
        return running;
    }

    public void queue(String command) {
        commands.add(command);
        SExpression exp = parse(command);

        var a = new Interpreter().eval(exp, SContext);
        if (a instanceof SExpression.SText text) {
            commandOutput.add(text.value());
        } else if (a != null) {
            commandOutput.add(a.toString());
        }
    }

    private static SExpression parse(String command) {
        return new Parser().parse(new Lexer().lex(command));
    }

    public ArrayList<String> getCommands() {
        return commands;
    }

    public ArrayList<String> getCommandOutput() {
        return commandOutput;
    }

    public ArrayList<String> getConsoleLog() {
        return consoleLog;
    }

    public void setActivePane(Object activePane) {
        this.activePane = panes.indexOf(activePane);
    }

    public void addAvailableActive(Object active) {
        this.panes.add(active);
    }

    public boolean isActive(Object o) {
        return panes.indexOf(o) == activePane;
    }

    public void tabActive() {
        this.activePane = (this.activePane + 1) % panes.size();
    }


    public static class ContextCallBackOperator extends SExpression.Lambda {
        private final Context context;

        public ContextCallBackOperator(Context context) {
            this.context = context;
        }

        @Override
        public SExpression eval(List<SExpression> rest, Interpreter interpreter, SContext definitions) {
            if (rest.isEmpty()) return null;
            if (rest.getFirst() instanceof SText test) {
                String text = test.value();
                if (text.equalsIgnoreCase("quit")) {
                    context.shutdown();
                } else if (text.equalsIgnoreCase("clear")) {
                    context.getCommandOutput().clear();
                } else if (text.equalsIgnoreCase("log")) {
                    if (rest.size() > 1) {
                        context.getConsoleLog().add(rest.get(1).toString());
                    }
                }
            }

            return null;
        }
    }
}
