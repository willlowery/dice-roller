package com.tomakeitgo;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class OutputLog implements TabPane.Panel {
    private final Supplier<List<String>> source;
    private int rowLength = 0;
    private int maxRows = 20;
    private int offset = 0;

    public OutputLog(Supplier<List<String>> source) {
        this.source = source;
    }

    public void resize(TerminalSize size) {
        this.rowLength = size.getColumns();
    }

    public void draw(Terminal terminal, boolean focused) throws IOException {
        var graphic = terminal.newTextGraphics();
        var ind = focused ? "+" : "|";

        var blankLine = ind + " ".repeat(rowLength - 1);
        var displayLines = buildDisplayLines();

        for (int i = 0; i < maxRows; i++) {
            if (i < displayLines.size() - offset) {
                String line = displayLines.get(displayLines.size() - i - 1 - offset);
                graphic.putString(new TerminalPosition(0, 3 + i), blankLine);
                graphic.putString(new TerminalPosition(1, 3 + i), line);
            } else {
                graphic.putString(new TerminalPosition(0, 3 + i), blankLine);
            }
        }
    }

    private List<String> buildDisplayLines() {
        var commands = source.get();
        var displayLines = new ArrayList<String>();
        int availableWidth = rowLength - 2; // account for indicator + space

        for (int i = 0; i < commands.size(); i++) {
            String prefix = (i + 1) + ": ";
            String indent = " ".repeat(prefix.length());
            String[] parts = commands.get(i).split("\n", -1);


            for (int k = parts.length-1; k >= 0; k--) {
                String part = parts[k];
                int contentWidth = availableWidth - prefix.length();

                List<String> reversed = Strings.chunk(part, contentWidth).reversed();
                for (int j = 0; j < reversed.size(); j++) {
                    String chunk = reversed.get(j);
                    displayLines.add(((j == reversed.size() - 1) && k == 0 ? prefix : indent) + chunk);
                }
            }
        }

        return displayLines;
    }

    public void input(KeyStroke stroke) {
        switch (stroke.getKeyType()) {
            case ArrowUp -> offset = Math.min(offset + 1, buildDisplayLines().size());
            case ArrowDown -> offset = Math.max(offset - 1, 0);
        }
    }
}
