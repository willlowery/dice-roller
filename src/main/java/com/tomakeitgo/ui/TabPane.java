package com.tomakeitgo.ui;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TabPane {
    private final TabBar tabBar;
    private final List<Panel> panels = new ArrayList<>();
    private int activeTab = 0;

    public TabPane() {
        this.tabBar = new TabBar();
    }

    public void addTab(String label, Panel panel) {
        tabBar.addTab(label);
        panels.add(panel);
    }

    public void resize(TerminalSize size) {
        tabBar.resize(size);
        for (Panel panel : panels) {
            panel.resize(size);
        }
    }

    public void draw(Terminal terminal, boolean focused) throws IOException {
        tabBar.draw(terminal, activeTab);
        if (!panels.isEmpty()) {
            panels.get(activeTab).draw(terminal, focused);
        }
    }

    public void input(KeyStroke stroke) {
        switch (stroke.getKeyType()) {
            case ArrowLeft -> activeTab = Math.max(0, activeTab - 1);
            case ArrowRight -> activeTab = Math.min(panels.size() - 1, activeTab + 1);
            default -> {
                if (!panels.isEmpty()) {
                    panels.get(activeTab).input(stroke);
                }
            }
        }
    }

    public interface Panel {
        void resize(TerminalSize size);
        void draw(Terminal terminal, boolean focused) throws IOException;
        void input(KeyStroke stroke);
    }
}
