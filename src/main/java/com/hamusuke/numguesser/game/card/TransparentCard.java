package com.hamusuke.numguesser.game.card;

public final class TransparentCard extends Card {
    public TransparentCard() {
        super(CardColor.BLACK);
    }

    @Override
    public int getNum() {
        return -2;
    }
}
