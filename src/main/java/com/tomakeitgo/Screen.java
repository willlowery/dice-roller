package com.tomakeitgo;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.IOException;


public class Screen {
    private final Terminal terminal;
    private final InputBar inputBar;
    private final TabBar tabBar;
    private final Separator clockSeparator = new Separator(2);
    private final OutputLog outputLog;
    private final Clock clockBar = new Clock();
    private final Context context;
    private final ConsoleLog consoleLog;

    public Screen(Terminal terminal, Context context)  {
        this.terminal = terminal;
        this.context = context;
        this.inputBar = new InputBar(context);
        this.outputLog = new OutputLog(context);
        this.tabBar = new TabBar(context);
        consoleLog = new ConsoleLog(context);

        context.addAvailableActive(inputBar);
        context.addAvailableActive(outputLog);
        context.addAvailableActive(consoleLog);

        context.setActivePane(inputBar);


        tabBar.addTab("Output", outputLog);
        tabBar.addTab("Console", consoleLog);
    }

    public void input(KeyStroke stroke) throws IOException {
        if (stroke.getKeyType().equals(KeyType.Tab)) {
            context.tabActive();
        } else if (context.isActive(inputBar)) {
            inputBar.input(stroke);
        } else if (context.isActive(outputLog)) {
            outputLog.input(stroke);
        }
    }


    public void resize(TerminalSize size) {
        clockBar.resize(size);
        tabBar.resize(size);
        clockSeparator.resize(size);

        consoleLog.resize(size);
        outputLog.resize(size);

        inputBar.resize(size);

        try {
            terminal.clearScreen();
        } catch (IOException e) {
            //ignored
        }
    }

    public void draw() throws IOException {
        clockBar.draw(terminal);
        tabBar.draw(terminal);
        clockSeparator.draw(terminal);

        if (context.isActive(consoleLog)) {
            consoleLog.draw(terminal);
        } else {
            outputLog.draw(terminal);
        }

        inputBar.draw(terminal);

        terminal.flush();
    }

}
