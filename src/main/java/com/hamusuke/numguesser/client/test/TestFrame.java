package com.hamusuke.numguesser.client.test;

import com.formdev.flatlaf.FlatDarkLaf;
import com.hamusuke.numguesser.client.game.card.AbstractClientCard;
import com.hamusuke.numguesser.client.game.card.ClientTransparentCard;
import com.hamusuke.numguesser.client.game.card.LocalCard;
import com.hamusuke.numguesser.client.gui.component.JXGameTablePanel;
import com.hamusuke.numguesser.client.gui.component.list.CardList;
import com.hamusuke.numguesser.client.gui.component.list.CardList.Direction;
import com.hamusuke.numguesser.client.gui.component.list.SinglePlayerGameCardList;
import com.hamusuke.numguesser.client.gui.component.panel.Panel;
import com.hamusuke.numguesser.client.gui.component.panel.main.play.GamePanel;
import com.hamusuke.numguesser.game.card.Card.CardColor;
import org.jdesktop.swingx.JXButton;
import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class TestFrame extends JXFrame {
    public TestFrame() {
        super("Test");

        var l = new GridBagLayout();
        this.setLayout(l);

        var table = new JXPanel(new BorderLayout(5, 5));
        var list = new DefaultListModel<AbstractClientCard>();
        list.addElement(new LocalCard(CardColor.BLACK, 5));
        list.addElement(new LocalCard(CardColor.WHITE, 8));
        list.addElement(new LocalCard(CardColor.WHITE, 10));

        var scroll = new JScrollPane(new CardList(Direction.SOUTH, list));
        table.add(scroll, Direction.SOUTH.layoutDir);
        var scroll2 = new JScrollPane(new CardList(Direction.NORTH, list));
        table.add(scroll2, Direction.NORTH.layoutDir);
        var scroll3 = new JScrollPane(new CardList(Direction.EAST, list));
        scroll3.getViewport().addChangeListener(e -> {
            //table.revalidate();
            this.repaint();
        });
        table.add(scroll3, Direction.EAST.layoutDir);
        table.add(new JScrollPane(new CardList(Direction.WEST, list)), Direction.WEST.layoutDir);
        var p = new JXPanel();
        p.add(new JXButton("aaa"));
        table.add(p, BorderLayout.CENTER);

        var p2 = new JXPanel();
        p2.add(table, BorderLayout.CENTER);
        p2.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
                table.setPreferredSize(p2.getSize());
                table.revalidate();
                table.repaint();
            }
        });
        Panel.addButton(this, p2, l, 0, 0, 1, 1, 1.0D);

        this.pack();
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    private static SinglePlayerGameCardList createList(int num) {
        var model = new DefaultListModel<AbstractClientCard>();
        model.addElement(new ClientTransparentCard());
        model.addElement(new ClientTransparentCard());
        model.addElement(new LocalCard(CardColor.BLACK, num));
        return new SinglePlayerGameCardList(model);
    }

    public static void main(String[] args) {
        FlatDarkLaf.setup();
        new TestFrame();
    }
}
