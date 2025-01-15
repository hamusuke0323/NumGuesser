package com.hamusuke.numguesser.client.game.card;

import org.jdesktop.swingx.JXPanel;

import java.awt.*;

import static com.hamusuke.numguesser.Constants.CARD_HEIGHT;
import static com.hamusuke.numguesser.Constants.CARD_WIDTH;

public class ClientTransparentCard extends AbstractClientCard {
    public ClientTransparentCard() {
        super(CardColor.BLACK);
    }

    @Override
    public int getNum() {
        return -2;
    }

    @Override
    public void setNum(int num) {
    }

    @Override
    public JXPanel toPanel(boolean isSelected, boolean cellHasFocus) {
        var ret = new JXPanel();
        var p = new JXPanel();
        p.setPreferredSize(new Dimension(CARD_WIDTH, CARD_HEIGHT));
        ret.add(p, BorderLayout.CENTER);
        return ret;
    }
}
