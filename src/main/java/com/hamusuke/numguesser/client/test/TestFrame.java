package com.hamusuke.numguesser.client.test;

import com.hamusuke.numguesser.client.game.card.AbstractClientCard;
import com.hamusuke.numguesser.client.game.card.ClientTransparentCard;
import com.hamusuke.numguesser.client.game.card.LocalCard;
import com.hamusuke.numguesser.client.gui.component.list.SinglePlayerGameCardList;
import com.hamusuke.numguesser.client.gui.component.panel.Panel;
import com.hamusuke.numguesser.game.card.Card.CardColor;
import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXPanel;

import javax.swing.*;
import java.awt.*;

public class TestFrame extends JXFrame {
    public TestFrame() {
        super("Test");

        var l = new GridBagLayout();
        this.setLayout(l);

        var p = new JXPanel(new GridLayout(0, 1));
        p.add(createList(5));
        p.add(createList(6));
        p.add(createList(9));
        p.add(createList(0));
        p.add(createList(2));

        Panel.addButton(this, new JScrollPane(p), l, 0, 0, 1, 1, 1.0D);

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
        new TestFrame();
    }
}
