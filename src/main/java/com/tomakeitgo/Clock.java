package com.tomakeitgo;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public class Clock {
    private final StringBuffer buffer = new StringBuffer();
    private final LocalDateTime applicationStart = LocalDateTime.now();

    public void resize(TerminalSize size) {
        this.buffer.setLength(size.getColumns());
    }

    public void draw(Terminal terminal) throws IOException {
        var now = LocalDateTime.now();
        var runtime = Duration.between(applicationStart, now).toMinutes();
        terminal
                .newTextGraphics()
                .putString(
                        new TerminalPosition(0, 0),
                        "Time: " + now.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)) + " Runtime: " + runtime + " minutes"
                );
    }
}
