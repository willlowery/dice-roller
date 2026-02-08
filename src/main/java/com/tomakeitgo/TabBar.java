package com.tomakeitgo;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TabBar {
    private final Context context;
    private final List<Tab> tabs = new ArrayList<>();
    private int columns = 0;

    public TabBar(Context context) {
        this.context = context;
    }

    public void addTab(String label, Object panel) {
        tabs.add(new Tab(label, panel));
    }

    public void resize(TerminalSize size) {
        this.columns = size.getColumns();
    }

    public void draw(Terminal terminal) throws IOException {
        var sb = new StringBuilder();
        for (Tab tab : tabs) {
            if (context.isActive(tab.panel)) {
                sb.append("[ ").append(tab.label).append(" ] ");
            } else {
                sb.append("  ").append(tab.label).append("   ");
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

    private record Tab(String label, Object panel) {}
}
