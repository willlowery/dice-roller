package com.tomakeitgo.ui;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.terminal.Terminal;
import com.tomakeitgo.Context;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FileEditor implements TabPane.Panel {
    private final Path filePath;
    private final List<StringBuilder> lines = new ArrayList<>();
    private final Context context;

    private int cursorRow = 0;
    private int cursorCol = 0;

    private int scrollRow = 0;
    private int scrollCol = 0;

    private int viewportRows = 20;
    private int viewportCols = 80;

    private boolean dirty = false;

    private static final int START_ROW = 3;
    private static final int GUTTER_WIDTH = 5;

    public FileEditor(Context context, Path filePath) {
        this.filePath = filePath;
        this.context = context;
        load();
    }

    private void load() {
        try {
            if(!Files.exists(filePath)){
                Files.createFile(filePath);
            }
            List<String> fileLines = Files.readAllLines(filePath);
            lines.clear();
            for (String line : fileLines) {
                lines.add(new StringBuilder(line));
            }
            if (lines.isEmpty()) {
                lines.add(new StringBuilder());
            }
        } catch (IOException e) {
            lines.clear();
            lines.add(new StringBuilder("Error loading file: " + e.getMessage()));
        }
        cursorRow = 0;
        cursorCol = 0;
        scrollRow = 0;
        scrollCol = 0;
        dirty = false;
    }

    @Override
    public void resize(TerminalSize size) {
        this.viewportCols = size.getColumns() - GUTTER_WIDTH;
        this.viewportRows = Math.min(19, size.getRows() - START_ROW - 3);
    }

    @Override
    public void draw(Terminal terminal, boolean focused) throws IOException {
        var graphics = terminal.newTextGraphics();
        String ind = focused ? "+" : "|";
        int totalWidth = viewportCols + GUTTER_WIDTH;

        // Status line: file path + dirty indicator
        String dirtyMark = dirty ? " [modified]" : "";
        String statusLine = ind + "    " + filePath.toAbsolutePath() + dirtyMark;
        if (statusLine.length() < totalWidth) {
            statusLine = statusLine + " ".repeat(totalWidth - statusLine.length());
        }
        
        graphics.putString(new TerminalPosition(0, START_ROW), statusLine);

        // File content starts one row below the status line
        int contentStartRow = START_ROW + 1;
        String blankLine = ind + " ".repeat(Math.max(0, totalWidth - 1));

        for (int i = 0; i < viewportRows; i++) {
            int fileRow = scrollRow + i;
            graphics.putString(new TerminalPosition(0, contentStartRow + i), blankLine);

            if (fileRow < lines.size()) {
                String lineNum = String.format("%3d ", fileRow + 1);
                graphics.putString(new TerminalPosition(1, contentStartRow + i), lineNum);

                StringBuilder line = lines.get(fileRow);
                int start = Math.min(scrollCol, line.length());
                int end = Math.min(scrollCol + viewportCols, line.length());
                if (start < end) {
                    graphics.putString(
                            new TerminalPosition(GUTTER_WIDTH, contentStartRow + i),
                            line.substring(start, end)
                    );
                }
            }
        }

        if (focused) {
            TerminalPosition position = new TerminalPosition(
                    GUTTER_WIDTH + (cursorCol - scrollCol),
                    contentStartRow + (cursorRow - scrollRow)
            );
            context.setCursor(position);
        }
    }

    @Override
    public void input(KeyStroke stroke) {
        switch (stroke.getKeyType()) {
            case Character -> {
                if (stroke.isCtrlDown() && stroke.getCharacter() == 's') {
                    save();
                } else {
                    insertCharacter(stroke.getCharacter());
                }
            }
            case Enter -> insertNewline();
            case Backspace -> deleteBack();
            case ArrowUp -> moveCursorUp();
            case ArrowDown -> moveCursorDown();
            case ArrowLeft -> moveCursorLeft();
            case ArrowRight -> moveCursorRight();
            case Home -> cursorCol = 0;
            case End -> cursorCol = lines.get(cursorRow).length();
        }
        adjustScroll();
    }

    private void insertCharacter(char c) {
        lines.get(cursorRow).insert(cursorCol, c);
        cursorCol++;
        dirty = true;
    }

    private void insertNewline() {
        StringBuilder currentLine = lines.get(cursorRow);
        String remainder = currentLine.substring(cursorCol);
        currentLine.delete(cursorCol, currentLine.length());
        lines.add(cursorRow + 1, new StringBuilder(remainder));
        cursorRow++;
        cursorCol = 0;
        dirty = true;
    }

    private void deleteBack() {
        if (cursorCol > 0) {
            lines.get(cursorRow).deleteCharAt(cursorCol - 1);
            cursorCol--;
            dirty = true;
        } else if (cursorRow > 0) {
            StringBuilder previousLine = lines.get(cursorRow - 1);
            cursorCol = previousLine.length();
            previousLine.append(lines.get(cursorRow));
            lines.remove(cursorRow);
            cursorRow--;
            dirty = true;
        }
    }

    private void moveCursorUp() {
        if (cursorRow > 0) {
            cursorRow--;
            cursorCol = Math.min(cursorCol, lines.get(cursorRow).length());
        }
    }

    private void moveCursorDown() {
        if (cursorRow < lines.size() - 1) {
            cursorRow++;
            cursorCol = Math.min(cursorCol, lines.get(cursorRow).length());
        }
    }

    private void moveCursorLeft() {
        if (cursorCol > 0) {
            cursorCol--;
        } else if (cursorRow > 0) {
            cursorRow--;
            cursorCol = lines.get(cursorRow).length();
        }
    }

    private void moveCursorRight() {
        if (cursorCol < lines.get(cursorRow).length()) {
            cursorCol++;
        } else if (cursorRow < lines.size() - 1) {
            cursorRow++;
            cursorCol = 0;
        }
    }

    private void adjustScroll() {
        if (cursorRow < scrollRow) {
            scrollRow = cursorRow;
        } else if (cursorRow >= scrollRow + viewportRows) {
            scrollRow = cursorRow - viewportRows + 1;
        }

        if (cursorCol < scrollCol) {
            scrollCol = cursorCol;
        } else if (cursorCol >= scrollCol + viewportCols) {
            scrollCol = cursorCol - viewportCols + 1;
        }
    }

    private void save() {
        try {
            List<String> output = new ArrayList<>();
            for (StringBuilder sb : lines) {
                output.add(sb.toString());
            }
            Files.write(filePath, output);
            dirty = false;
        } catch (IOException ignored) {
        }
    }
}
