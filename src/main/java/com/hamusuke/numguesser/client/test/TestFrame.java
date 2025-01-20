package com.hamusuke.numguesser.client.test;

import com.hamusuke.numguesser.client.game.card.AbstractClientCard;
import com.hamusuke.numguesser.client.game.card.ClientTransparentCard;
import com.hamusuke.numguesser.client.game.card.LocalCard;
import com.hamusuke.numguesser.client.gui.component.JXGameTablePanel;
import com.hamusuke.numguesser.client.gui.component.list.SinglePlayerGameCardList;
import com.hamusuke.numguesser.client.gui.component.panel.Panel;
import com.hamusuke.numguesser.game.card.Card.CardColor;
import org.jdesktop.swingx.JXFrame;

import javax.swing.*;
import java.awt.*;

public class TestFrame extends JXFrame {
    public TestFrame() {
        super("Test");

        var l = new GridBagLayout();
        this.setLayout(l);

        var table = new JXGameTablePanel();
        var list = new DefaultListModel<AbstractClientCard>();
        list.addElement(new LocalCard(CardColor.BLACK, 5));
        list.addElement(new LocalCard(CardColor.WHITE, 8));
        list.addElement(new LocalCard(CardColor.WHITE, 10));
        table.addCardList(true, list);
        table.addCardList(false, list);
        table.addCardList(false, list);
        table.addCardList(false, list);

        Panel.addButton(this, table, l, 0, 0, 1, 1, 1.0D);

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
