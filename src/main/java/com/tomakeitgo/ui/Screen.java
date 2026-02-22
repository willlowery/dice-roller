package com.tomakeitgo.ui;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.terminal.Terminal;
import com.tomakeitgo.Context;

import java.io.IOException;


public class Screen {
    private final Terminal terminal;
    private final InputBar inputBar;
    private final Separator clockSeparator = new Separator(2);
    private final TabPane tabPane;
    private final Clock clockBar = new Clock();
    private final Context context;

    public Screen(Terminal terminal, Context context) {
        this.terminal = terminal;
        this.context = context;
        this.inputBar = new InputBar(context);
        this.tabPane = new TabPane();

        tabPane.addTab("Output", new OutputLog(context::getCommandOutput));
        tabPane.addTab("Console", new OutputLog(context::getConsoleLog));
        
        context.addAvailableActive(inputBar);
        context.addAvailableActive(tabPane);
        context.setActivePane(inputBar);
    }

    public TabPane getTabPane() {
        return tabPane;
    }

    public void input(KeyStroke stroke) throws IOException {
        if (stroke.getKeyType().equals(KeyType.Tab)) {
            context.tabActive();
        } else if (context.isActive(inputBar)) {
            inputBar.input(stroke);
        } else if (context.isActive(tabPane)) {
            tabPane.input(stroke);
        }
    }

    public void resize(TerminalSize size) {
        clockBar.resize(size);
        clockSeparator.resize(size);
        tabPane.resize(size);
        inputBar.resize(size);

        try {
            terminal.clearScreen();
        } catch (IOException e) {
            //ignored
        }
    }

    public void draw() throws IOException {
        clockBar.draw(terminal);
        clockSeparator.draw(terminal);
        inputBar.draw(terminal);
        tabPane.draw(terminal, context.isActive(tabPane));
        
        if (context.isActive(inputBar)) {
            terminal.setCursorPosition(inputBar.getCursorPosition());
        }
        terminal.flush();
        
    }
}
