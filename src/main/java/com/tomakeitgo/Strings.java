package com.tomakeitgo;

import java.util.ArrayList;
import java.util.List;

public class Strings {
    public static List<String> chunk(String text, int width) {
        List<String> chunks = new ArrayList<>();
        if (width > 0) {
            for (int pos = 0; pos < text.length(); pos += width) {
                chunks.add(text.substring(pos, Math.min(pos + width, text.length())));
            }
        }
        if (chunks.isEmpty()) {
            chunks.add("");
        }
        return chunks;
    }
}
