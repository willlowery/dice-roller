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
    private final ArrayList<String> commandOutput = new ArrayList<>();
    private int active;
    private final List<Object> availableActive = new ArrayList<>();
    private final SContext SContext = Interpreter.createSContext();

    public Context() {
        SContext.register(new SExpression.SAtom("host/send"), new ContextCallBackOperator(this));
    }

    public void shutdown() {
        running = false;
    }

    public boolean isRunning() {
        return running;
    }

    public void queue(String command) {
        commands.add(command);
        SExpression exp = new Parser().parse(new Lexer().lex(command));

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

    public static class ContextCallBackOperator extends SExpression.Lambda {
        private final Context context;
    
        public ContextCallBackOperator(Context context) {
            this.context = context;
        }
    
        @Override
        public SExpression eval(List<SExpression> rest, Interpreter interpreter, SContext definitions) {
            if ( rest.isEmpty()) return null;
            if (rest.getFirst() instanceof SText test){
                String text = test.value();
                if (text.equalsIgnoreCase("quit")){
                    context.shutdown();
                } else if (text.equalsIgnoreCase("clear")) {
                    context.getCommandOutput().clear();
                }
            }
            
            return null;
        }
    }
}
