package com.tomakeitgo.ui;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ScrollBufferTest {

    private ScrollBuffer buffer(String prefix) {
        return new ScrollBuffer(prefix);
    }

    private ScrollBuffer bufferWithText(String text) {
        var sb = new ScrollBuffer(" > ");
        for (char c : text.toCharArray()) {
            sb.insert(c);
        }
        return sb;
    }

    // --- Insert & getText ---

    @Test
    void insertSingleCharacter() {
        var sb = buffer(" > ");
        sb.insert('a');
        assertEquals("a", sb.getText());
    }

    @Test
    void insertMultipleCharacters() {
        var sb = bufferWithText("hello");
        assertEquals("hello", sb.getText());
    }

    @Test
    void insertAtMiddle() {
        var sb = bufferWithText("ac");
        sb.moveCursorLeft();
        sb.insert('b');
        assertEquals("abc", sb.getText());
    }

    // --- deleteBack ---

    @Test
    void deleteLastCharacter() {
        var sb = bufferWithText("ab");
        sb.deleteBack();
        assertEquals("a", sb.getText());
    }

    @Test
    void deleteFromMiddle() {
        var sb = bufferWithText("abc");
        sb.moveCursorLeft();
        sb.deleteBack();
        assertEquals("ac", sb.getText());
    }

    @Test
    void deleteAtStartIsNoop() {
        var sb = bufferWithText("abc");
        sb.moveCursorToStart();
        sb.deleteBack();
        assertEquals("abc", sb.getText());
    }

    // --- Cursor Movement ---

    @Test
    void moveCursorLeftDecrementsPosition() {
        var sb = bufferWithText("ab");
        int before = sb.getCursorScreenPosition();
        sb.moveCursorLeft();
        assertEquals(before - 1, sb.getCursorScreenPosition());
    }

    @Test
    void moveCursorLeftAtStartIsNoop() {
        var sb = bufferWithText("a");
        sb.moveCursorToStart();
        int pos = sb.getCursorScreenPosition();
        sb.moveCursorLeft();
        assertEquals(pos, sb.getCursorScreenPosition());
    }

    @Test
    void moveCursorRightIncrementsPosition() {
        var sb = bufferWithText("ab");
        sb.moveCursorToStart();
        int before = sb.getCursorScreenPosition();
        sb.moveCursorRight();
        assertEquals(before + 1, sb.getCursorScreenPosition());
    }

    @Test
    void moveCursorRightAtEndIsNoop() {
        var sb = bufferWithText("ab");
        int pos = sb.getCursorScreenPosition();
        sb.moveCursorRight();
        assertEquals(pos, sb.getCursorScreenPosition());
    }

    @Test
    void moveCursorToStart() {
        var sb = bufferWithText("hello");
        sb.moveCursorToStart();
        assertEquals(3, sb.getCursorScreenPosition()); // " > " prefix length
    }

    @Test
    void moveCursorToEnd() {
        var sb = bufferWithText("hello");
        sb.moveCursorToStart();
        sb.moveCursorToEnd();
        assertEquals(8, sb.getCursorScreenPosition()); // " > " (3) + "hello" (5)
    }

    // --- setContent & reset ---

    @Test
    void setContentReplacesText() {
        var sb = bufferWithText("old");
        sb.setContent("new");
        assertEquals("new", sb.getText());
    }

    @Test
    void setContentOverwritesPrevious() {
        var sb = buffer(" > ");
        sb.setContent("first");
        sb.setContent("second");
        assertEquals("second", sb.getText());
    }

    @Test
    void resetClearsText() {
        var sb = bufferWithText("hello");
        sb.reset();
        assertEquals("", sb.getText());
    }

    @Test
    void resetMovesCursorToStart() {
        var sb = bufferWithText("hello");
        sb.reset();
        assertEquals(3, sb.getCursorScreenPosition()); // prefix length
    }

    // --- Scrolling ---

    @Test
    void typingPastVisibleWidthScrolls() {
        var sb = buffer(" > ");
        sb.setVisibleWidth(10);
        for (char c : "abcdefghijklmno".toCharArray()) {
            sb.insert(c);
        }
        int pos = sb.getCursorScreenPosition();
        assertTrue(pos >= 0 && pos < 10);
    }

    @Test
    void visibleSliceReturnsWindowOfText() {
        var sb = buffer(" > ");
        sb.setVisibleWidth(10);
        for (char c : "abcdefghijklmno".toCharArray()) {
            sb.insert(c);
        }
        String slice = sb.getVisibleSlice();
        assertEquals(10, slice.length());
    }

    @Test
    void visibleSlicePaddedWhenTextShorter() {
        var sb = buffer(" > ");
        sb.setVisibleWidth(20);
        sb.insert('a');
        String slice = sb.getVisibleSlice();
        assertEquals(20, slice.length());
    }

    @Test
    void moveCursorLeftPastScrollBoundary() {
        var sb = buffer(" > ");
        sb.setVisibleWidth(10);
        for (char c : "abcdefghijklmno".toCharArray()) {
            sb.insert(c);
        }
        sb.moveCursorToStart();
        // scroll adjusts to cursor (prefix.length()), screen position is 0
        assertEquals(0, sb.getCursorScreenPosition());
        // visible slice starts at cursor position (beginning of user text)
        assertTrue(sb.getVisibleSlice().startsWith("a"));
    }

    @Test
    void moveCursorToStartAfterScrolling() {
        var sb = buffer(" > ");
        sb.setVisibleWidth(10);
        for (char c : "abcdefghijklmno".toCharArray()) {
            sb.insert(c);
        }
        sb.moveCursorToStart();
        // cursor is at prefix boundary, scroll aligns to it
        assertEquals(0, sb.getCursorScreenPosition());
        assertEquals("abcdefghijklmno", sb.getText());
    }

    @Test
    void moveCursorToEndOnLongText() {
        var sb = buffer(" > ");
        sb.setVisibleWidth(10);
        sb.setContent("abcdefghijklmno");
        sb.moveCursorToEnd();
        int pos = sb.getCursorScreenPosition();
        assertTrue(pos >= 0 && pos < 10);
        // Visible slice should contain the end of the text
        assertTrue(sb.getVisibleSlice().contains("o"));
    }

    @Test
    void setVisibleWidthAdjustsScroll() {
        var sb = buffer(" > ");
        sb.setVisibleWidth(10);
        for (char c : "abcdefghijklmno".toCharArray()) {
            sb.insert(c);
        }
        sb.setVisibleWidth(30);
        int pos = sb.getCursorScreenPosition();
        assertTrue(pos >= 0 && pos < 30);
    }

    // --- Edge Cases ---

    @Test
    void emptyPrefix() {
        var sb = buffer("");
        sb.insert('a');
        assertEquals("a", sb.getText());
        assertEquals(1, sb.getCursorScreenPosition());
    }

    @Test
    void setContentLongerThanVisibleWidth() {
        var sb = buffer(" > ");
        sb.setVisibleWidth(10);
        sb.setContent("abcdefghijklmnopqrstuvwxyz");
        assertEquals("abcdefghijklmnopqrstuvwxyz", sb.getText());
    }
}
