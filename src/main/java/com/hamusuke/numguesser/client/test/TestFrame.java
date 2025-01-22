package com.hamusuke.numguesser.client.test;

import com.formdev.flatlaf.FlatDarkLaf;
import com.hamusuke.numguesser.client.game.SinglePlayerGame;
import com.hamusuke.numguesser.client.game.SinglePlayerGame.Difficulty;
import com.hamusuke.numguesser.client.gui.component.panel.Panel;
import com.hamusuke.numguesser.client.gui.component.table.SinglePlayerGameTable;
import org.jdesktop.swingx.JXButton;
import org.jdesktop.swingx.JXFrame;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;

public class TestFrame extends JXFrame {
    public TestFrame() {
        super("Test");

        var l = new GridBagLayout();
        this.setLayout(l);

        var add = new JXButton("Set");
        add.setEnabled(false);
        var diff = Difficulty.HARD;
        var game = new SinglePlayerGame(diff);
        var table = new SinglePlayerGameTable(diff) {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                super.valueChanged(e);

            }

            @Override
            public void columnSelectionChanged(ListSelectionEvent e) {
                super.columnSelectionChanged(e);

                SwingUtilities.invokeLater(() -> {
                    var row = this.getSelectedRow();
                    var col = this.getSelectedColumn();
                    if (row < 0 || col < 0 || !this.isValidPos(row, col)) {
                        add.setEnabled(false);
                        return;
                    }

                    add.setEnabled(true);
                });
            }
        };

        add.addActionListener(e -> {
            var row = table.getSelectedRow();
            var col = table.getSelectedColumn();
            if (row < 0 || col < 0 || !table.isValidPos(row, col)) {
                return;
            }

        });

        for (int i = 0; i < diff.firstOpenedCardNum; i++) {
            var col = diff.centerIndex;
        }

        var scroll = new JScrollPane(table);

        Panel.addButton(this, scroll, l, 0, 0, 1, 1, 1.0D);
        Panel.addButton(this, add, l, 0, 1, 1, 1, 1.0D, 0.05D);

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
