package com.hamusuke.numguesser.client.test;

import com.formdev.flatlaf.FlatDarkLaf;
import org.jdesktop.swingx.JXFrame;

import java.awt.*;

public class TestFrame extends JXFrame {
    public TestFrame() {
        super("Test");

        var l = new GridBagLayout();
        this.setLayout(l);

        this.pack();
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    public static void main(String[] args) {
        FlatDarkLaf.setup();
        new TestFrame();
    }
}
