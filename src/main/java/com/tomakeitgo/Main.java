package com.tomakeitgo;

import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import com.tomakeitgo.ui.Screen;

public class Main {
    public static void main(String[] args) throws Exception {
        var context = new Context();
        
        DefaultTerminalFactory defaultTerminalFactory = new DefaultTerminalFactory();
        defaultTerminalFactory.setTerminalEmulatorTitle("Dice");
        
        Terminal terminal = defaultTerminalFactory.createTerminal();
        var screen = new Screen(terminal, context);
        terminal.addResizeListener((t, size) -> screen.resize(size));

        terminal.enterPrivateMode();
        terminal.clearScreen();
        terminal.setCursorVisible(true);
        
        screen.resize(terminal.getTerminalSize());
        screen.draw();
        
        var inputThread = new Thread(() -> {
            while (context.isRunning()) {
                try {
                    KeyStroke stroke = terminal.readInput();
                    if (stroke.getKeyType().equals(KeyType.EOF)) {
                        context.shutdown();
                        break;
                    }
                    screen.input(stroke);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        var renderThread = new Thread(() -> {
            while (context.isRunning()) {
                try {
                    var start = System.currentTimeMillis();
                    screen.draw();
                    var end = System.currentTimeMillis();
                    var elapsedTime = end - start;

                    Thread.sleep(Math.max((1000 / 10) - elapsedTime, 0));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        inputThread.start();
        renderThread.start();

        inputThread.join();
        renderThread.join();

        terminal.close();
    }

}