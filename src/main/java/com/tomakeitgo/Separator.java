package com.tomakeitgo;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.IOException;

public class Separator {
    private final int row;
    private String line = "";

    public Separator(int row) {
        this.row = row;
    }

    public void resize(TerminalSize size) {
        this.line = "-".repeat(size.getColumns());
    }

    public void draw(Terminal terminal) throws IOException {
        terminal
                .newTextGraphics()
                .putString(
                        new TerminalPosition(0, row),
                        line
                );
    }
}
