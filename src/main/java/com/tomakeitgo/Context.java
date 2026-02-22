package com.tomakeitgo;

import com.googlecode.lanterna.TerminalPosition;
import com.tomakeitgo.lisp.*;
import com.tomakeitgo.ui.FileEditor;
import com.tomakeitgo.ui.Screen;
import com.tomakeitgo.ui.TabPane;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Context {
    private final Interpreter interpreter;
    private boolean running = true;
    private final ArrayList<String> commands = new ArrayList<>();
    private final ArrayList<String> consoleLog = new ArrayList<>();
    private final ArrayList<String> commandOutput = new ArrayList<>();
    private int activePane;
    private final List<Object> panes = new ArrayList<>();
    private final SContext root;
    private SContext active;
    
    private final HashMap<SExpression, SContext> contexts = new HashMap<>();
    private Screen screen;
    private TerminalPosition cursor = new TerminalPosition(0,0);

    public Context() {
        var importBaseDirectory = Path.of(System.getProperty("user.dir"));

        interpreter = new Interpreter(importBaseDirectory);
        root = interpreter.createSContext();
        active = root;
        
        contexts.put(new SExpression.SText("/"), root);
        active.register(new SExpression.SAtom("host/send"), new ContextCallBackOperator(this));


        interpreter.eval(parse("(def log (lambda (x) (host/send 'log' x)))"), active);
        interpreter.eval(parse("(def clear (lambda () (host/send 'clear')))"), active);
        interpreter.eval(parse("(def quit (lambda () (host/send 'quit')))"), active);
        interpreter.eval(parse("(def swap (lambda (x) (host/send 'setContext' x)))"), active);
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

        var a = interpreter.eval(exp, active);
        if (a instanceof SExpression.SText text) {
            commandOutput.add(text.value());
        } else if (a != null) {
            commandOutput.add(a.toString());
        }
    }

    private static SExpression parse(String command) {
        return new Parser().parse(new Lexer().lex(command));
    }

    public static List<SExpression> parseAll(String command) {
        return new Parser().parseAll(new Lexer().lex(command));
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

    public void setScreen(Screen screen) {
        this.screen = screen;
    }

    public void setCursor(TerminalPosition cursor) {
        this.cursor = cursor;
    }

    public TerminalPosition getCursor() {
        return cursor;
    }

    public static class ContextCallBackOperator implements SExpression.Operator {
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
                } else if (text.equalsIgnoreCase("setContext")) {
                    context.active = context.contexts.computeIfAbsent(
                            rest.get(1),
                            (i) -> context.root.copy()
                    );
                    return new SText("Context set to " + rest.get(1).toString());
                } else if (text.equalsIgnoreCase("open") && rest.size() > 1 && rest.get(1) instanceof SText toOpen) {
                    TabPane tabPane = context.screen.getTabPane();
                    context.setActivePane(context.screen.getTabPane());
                    FileEditor panel = new FileEditor(context, interpreter.getImportBaseDirectory().resolve(toOpen.value()));
                    tabPane.addTab(toOpen.value(), panel);
                    tabPane.setActive(panel);
                }
            }

            return null;
        }
    }
}
