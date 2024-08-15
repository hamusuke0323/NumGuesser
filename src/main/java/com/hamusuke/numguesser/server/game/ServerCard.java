package com.hamusuke.numguesser.server.game;

import com.hamusuke.numguesser.game.card.Card;

public class ServerCard extends Card {
    private final int num;

    public ServerCard(CardColor cardColor, int num) {
        super(cardColor);
        this.num = num;
    }

    @Override
    public int getNum() {
        return this.num;
    }
}
