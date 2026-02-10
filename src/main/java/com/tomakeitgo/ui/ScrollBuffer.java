package com.tomakeitgo.ui;

public class ScrollBuffer {
    private final String prefix;
    private final StringBuilder buffer = new StringBuilder();
    private int cursor;
    private int scroll = 0;
    private int visibleWidth = 1024;

    public ScrollBuffer(String prefix) {
        this.prefix = prefix;
        reset();
    }

    public void insert(char c) {
        buffer.insert(cursor, c);
        moveCursorRight();
        adjustScroll();
    }

    public void deleteBack() {
        if (cursor > prefix.length()) {
            moveCursorLeft();
            buffer.deleteCharAt(cursor);
            adjustScroll();
        }
    }

    public void moveCursorLeft() {
        cursor = Math.max(cursor - 1, prefix.length());
        adjustScroll();
    }

    public void moveCursorRight() {
        cursor = Math.min(cursor + 1, buffer.length());
        adjustScroll();
    }

    public void moveCursorToStart() {
        cursor = prefix.length();
        adjustScroll();
    }

    public void moveCursorToEnd() {
        cursor = buffer.length();
        adjustScroll();
    }

    public void setContent(String text) {
        reset();
        buffer.insert(prefix.length(), text);
        adjustScroll();
    }

    public String getText() {
        return buffer.substring(prefix.length()).trim();
    }

    public String getVisibleSlice() {
        int end = Math.min(buffer.length(), scroll + visibleWidth);
        String visible = buffer.substring(scroll, end);
        if (visible.length() < visibleWidth) {
            visible = visible + " ".repeat(visibleWidth - visible.length());
        }
        return visible;
    }

    public int getCursorScreenPosition() {
        return cursor - scroll;
    }

    public void setVisibleWidth(int width) {
        this.visibleWidth = width;
        adjustScroll();
    }

    public void reset() {
        buffer.setLength(0);
        buffer.append(prefix);
        cursor = prefix.length();
        scroll = 0;
    }

    private void adjustScroll() {
        if (cursor < scroll) {
            scroll = cursor;
        } else if (cursor >= scroll + visibleWidth) {
            scroll = cursor - visibleWidth + 1;
        }
    }
}
