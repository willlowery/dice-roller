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
    private final ScrollBuffer scrollBuffer = new ScrollBuffer(" > ");
    private int historyIndex = 0;
    private int location = 0;

    public InputBar(Context context) {
        this.context = context;
    }

    public void input(KeyStroke stroke) {
        switch (stroke.getKeyType()) {
            case Character  -> scrollBuffer.insert(stroke.getCharacter());
            case Backspace  -> scrollBuffer.deleteBack();
            case ArrowLeft  -> moveLeft(stroke);
            case ArrowRight -> moveRight(stroke);
            case ArrowUp    -> historyBack();
            case ArrowDown  -> historyForward();
            case Enter      -> submit();
        }
    }

    private void moveLeft(KeyStroke stroke) {
        if (stroke.isAltDown()) {
            scrollBuffer.moveCursorToStart();
        } else {
            scrollBuffer.moveCursorLeft();
        }
    }

    private void moveRight(KeyStroke stroke) {
        if (stroke.isAltDown()) {
            scrollBuffer.moveCursorToEnd();
        } else {
            scrollBuffer.moveCursorRight();
        }
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
            scrollBuffer.reset();
        } else {
            historyIndex = Math.max(0, historyIndex - 1);
            loadHistory(historyIndex);
        }
    }

    private void loadHistory(int targetIndex) {
        ArrayList<String> commands = context.getCommands();
        scrollBuffer.reset();
        if (targetIndex >= 0 && targetIndex < commands.size()) {
            scrollBuffer.setContent(commands.get(commands.size() - targetIndex - 1));
        }
    }

    private void submit() {
        String command = scrollBuffer.getText();
        context.queue(command);
        historyIndex = 0;
        scrollBuffer.reset();
    }

    public void resize(TerminalSize size) {
        scrollBuffer.setVisibleWidth(size.getColumns());
        this.location = size.getRows();
    }

    public void draw(Terminal terminal) throws IOException {
        boolean isActive = context.isActive(this);
        var ind = isActive ? "+" : "|";
        terminal
                .newTextGraphics()
                .putString(
                        new TerminalPosition(0, location - 1),
                        ind + scrollBuffer.getVisibleSlice()
                );
        if (isActive){
            context.setCursor(new TerminalPosition(scrollBuffer.getCursorScreenPosition() + 1, location - 1));
        }
    }

}
