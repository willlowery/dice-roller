package com.tomakeitgo.ui;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.terminal.Terminal;
import com.tomakeitgo.Context;

import java.io.IOException;
import java.util.ArrayList;

public class InputBar {
    private final Context context;
    private final String indicator = " > ";
    private int index = indicator.length();
    private int historyIndex = 0;
    private int location = 0;
    private final StringBuilder buffer = new StringBuilder();
    private int columns = 1024;
    private int scroll = 0;

    public InputBar(Context context) {
        this.context = context;
    }

    public void input(KeyStroke stroke) {
        switch (stroke.getKeyType()) {
            case Character  -> insertCharacter(stroke.getCharacter());
            case Backspace  -> backspace();
            case ArrowLeft  -> moveLeft(stroke);
            case ArrowRight -> moveRight(stroke);
            case ArrowUp    -> historyBack();
            case ArrowDown  -> historyForward();
            case Enter      -> submit();
        }
    }

    private void insertCharacter(char c) {
        int currentLength = buffer.length();
        if(index > currentLength) {
            buffer.setLength(index);
            for (int i = currentLength; i < index; i++) {
                buffer.setCharAt(i, ' ');
            }
        }
        buffer.insert(index, c);
        increment();
        adjustScroll();
    }

    private void backspace() {
        if (index > indicator.length()) {
            decrement();
            buffer.deleteCharAt(index);
            adjustScroll();
        }
    }

    private void moveLeft(KeyStroke stroke) {
        if (stroke.isAltDown()) {
            index = indicator.length();
        } else {
            decrement();
        }
        adjustScroll();
    }

    private void moveRight(KeyStroke stroke) {
        if (stroke.isAltDown()) {
            index = buffer.length();
        } else {
            increment();
        }
        adjustScroll();
    }

    private void historyBack() {
        ArrayList<String> commands = context.getCommands();
        if (commands.size() > historyIndex) {
            loadHistory(historyIndex);
        }
        historyIndex = Math.min(commands.size() - 1, historyIndex + 1);
    }

    private void historyForward() {
        if (historyIndex == 0) {
            resetBuffer();
        } else {
            historyIndex = Math.max(0, historyIndex - 1);
            loadHistory(historyIndex);
        }
    }

    private void loadHistory(int targetIndex) {
        ArrayList<String> commands = context.getCommands();
        resetBuffer();
        if (targetIndex >= 0 && targetIndex < commands.size()) {
            buffer.insert(indicator.length(), commands.get(commands.size() - targetIndex - 1));
        }
        adjustScroll();
    }

    private void submit() {
        String command = buffer.substring(indicator.length()).trim();
        context.queue(command);
        historyIndex = 0;
        resetBuffer();
    }

    private void increment() {
        index = Math.min(index + 1, buffer.length());
    }

    private void decrement() {
        index = Math.max(index - 1, indicator.length());
    }

    private void adjustScroll() {
        int visibleWidth = columns;
        if (index < scroll) {
            scroll = index;
        } else if (index >= scroll + visibleWidth) {
            scroll = index - visibleWidth + 1;
        }
    }

    public void resize(TerminalSize size) {
        this.columns = size.getColumns();
        this.location = size.getRows();
        adjustScroll();
    }

    private void resetBuffer() {
        buffer.setLength(0);
        buffer.append(indicator);
        this.index = indicator.length();
        this.scroll = 0;
    }

    public void draw(Terminal terminal) throws IOException {
        var ind = context.isActive(this) ? "+" : "|";
        int visibleWidth = columns;
        int end = Math.min(buffer.length(), scroll + visibleWidth);
        String visible = buffer.substring(scroll, end);
        if (visible.length() < visibleWidth) {
            visible = visible + " ".repeat(visibleWidth - visible.length());
        }
        terminal
                .newTextGraphics()
                .putString(
                        new TerminalPosition(0, location - 1),
                        ind + visible
                );
        terminal.setCursorPosition(new TerminalPosition(index - scroll + 1, location - 1));
    }
}
