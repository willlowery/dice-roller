package com.tomakeitgo;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TabBar {
    private final List<String> labels = new ArrayList<>();
    private int columns = 0;

    public void addTab(String label) {
        labels.add(label);
    }

    public void resize(TerminalSize size) {
        this.columns = size.getColumns();
    }

    public void draw(Terminal terminal, int activeTab) throws IOException {
        var sb = new StringBuilder();
        for (int i = 0; i < labels.size(); i++) {
            if (i == activeTab) {
                sb.append("[ ").append(labels.get(i)).append(" ] ");
            } else {
                sb.append("  ").append(labels.get(i)).append("   ");
            }
        }
        String line = sb.toString();
        if (line.length() < columns) {
            line = line + " ".repeat(columns - line.length());
        }
        terminal
                .newTextGraphics()
                .putString(new TerminalPosition(0, 1), line);
    }
}
