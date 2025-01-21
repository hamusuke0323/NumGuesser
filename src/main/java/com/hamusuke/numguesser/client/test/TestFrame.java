package com.hamusuke.numguesser.client.test;

import com.formdev.flatlaf.FlatDarkLaf;
import com.hamusuke.numguesser.client.game.card.AbstractClientCard;
import com.hamusuke.numguesser.client.game.card.ClientTransparentCard;
import com.hamusuke.numguesser.client.game.card.LocalCard;
import com.hamusuke.numguesser.client.gui.component.list.SinglePlayerGameCardList;
import com.hamusuke.numguesser.client.gui.component.panel.Panel;
import com.hamusuke.numguesser.client.gui.component.table.SinglePlayerGameTable;
import com.hamusuke.numguesser.game.card.Card.CardColor;
import com.hamusuke.numguesser.game.single.SinglePlayerGame;
import com.hamusuke.numguesser.game.single.SinglePlayerGame.Difficulty;
import org.jdesktop.swingx.JXFrame;

import javax.swing.*;
import java.awt.*;

public class TestFrame extends JXFrame {
    public TestFrame() {
        super("Test");

        var l = new GridBagLayout();
        this.setLayout(l);

        var game = new SinglePlayerGame<AbstractClientCard>(Difficulty.EASY);
        game.start();
        var table = new SinglePlayerGameTable(Difficulty.EASY);

        for (int i = 0; i < Difficulty.EASY.firstOpenedCardNum; i++) {
            var col = Difficulty.EASY.centerIndex;
            table.setCardTo(game.cardTable.get(i).get(col), i, col);
        }

        var scroll = new JScrollPane(table);

        Panel.addButton(this, scroll, l, 0, 0, 1, 1, 1.0D);

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
