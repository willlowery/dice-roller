package com.tomakeitgo;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.IOException;
import java.util.ArrayList;

public class OutputLog {
    private final Context context;
    private int rowLength = 0;
    private int maxRows = 20;
    private int offset = 0;

    public OutputLog(Context context) {
        this.context = context;
    }

    public void resize(TerminalSize size) {
        this.rowLength = size.getColumns();
    }

    public void draw(Terminal terminal) throws IOException {
        var graphic = terminal.newTextGraphics();
        var ind = context.isActive(this) ? "+" : "|";

        var blankLine = ind + " ".repeat(rowLength - 1);
        var commands = context.getCommandOutput();

        for (int i = 0; i < maxRows; i++) {
            if (i < commands.size() - offset) {
                String command =  (commands.size() - i - offset) + ": " + commands.get(commands.size() - i - 1 - offset);
                graphic.putString(new TerminalPosition(0, 2 + i), blankLine);
                graphic.putString(new TerminalPosition(1, 2 + i), command);
            } else {
                graphic.putString(new TerminalPosition(0, 2 + i), blankLine);
            }
        }
    }

    public void input(KeyStroke stroke) {
        switch (stroke.getKeyType()) {
            case ArrowUp -> offset = Math.min(offset + 1, context.getCommandOutput().size());
            case ArrowDown -> offset = Math.max(offset - 1, 0);
        }
    }
}
