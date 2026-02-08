package com.tomakeitgo;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.IOException;
import java.util.ArrayList;

public class InputBar {
    private final Context context;
    private final String indicator = " > ";
    private int index = indicator.length() ;
    private int historyIndex = 0;
    private int location = 0;
    private final StringBuilder buffer = new StringBuilder();
    private int columns = 1024;

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
        buffer.insert(index, c);
        buffer.setLength(columns);
        increment();
    }

    private void backspace() {
        if (index > indicator.length()) {
            decrement();
            buffer.deleteCharAt(index);
            buffer.setLength(columns);
        }
    }

    private void moveLeft(KeyStroke stroke) {
        if (stroke.isAltDown()) {
            index = indicator.length();
        } else {
            decrement();
        }
    }

    private void moveRight(KeyStroke stroke) {
        if (stroke.isAltDown()) {
            int end = buffer.length();
            while (end > indicator.length() && Character.isSpaceChar(buffer.charAt(end - 1))) {
                end--;
            }
            index = end;
        } else {
            increment();
        }
    }

    private void historyBack() {
        ArrayList<String> commands = context.getCommands();
        if (commands.size() > historyIndex) {
            resetBuffer();
            buffer.insert(indicator.length(), commands.get(commands.size() - historyIndex - 1));
        }
        historyIndex = Math.min(commands.size() - 1, historyIndex + 1);
    }

    private void historyForward() {
        if (historyIndex == 0) {
            resetBuffer();
        } else {
            ArrayList<String> commands = context.getCommands();
            historyIndex = Math.max(0, historyIndex - 1);
            if (commands.size() > historyIndex) {
                resetBuffer();
                buffer.insert(indicator.length(), commands.get(commands.size() - historyIndex - 1));
            }
        }
    }

    private void submit() {
        String command = buffer
                .substring(indicator.length())
                .replace((char) 0, ' ')
                .trim();

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

    public void resize(TerminalSize size) {
        this.columns = size.getColumns();
        this.location = size.getRows();
        resetBuffer();
    }

    private void resetBuffer() {
        buffer.setLength(0);
        buffer.append(indicator);
        buffer.append(" ".repeat(columns - indicator.length()));
        this.index = indicator.length();
    }

    public void draw(Terminal terminal) throws IOException {
        var ind = context.isActive(this) ? "+" : "|" ;
        terminal
                .newTextGraphics()
                .putString(
                        new TerminalPosition(0, location - 1),
                        ind + buffer.toString().replace((char) 0, ' ')
                );
        terminal.setCursorPosition(new TerminalPosition(index+1, location - 1));
    }
}