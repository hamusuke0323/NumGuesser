package com.hamusuke.numguesser.client.game.card;

public class LocalCard extends AbstractClientCard {
    private final int num;

    public LocalCard(CardColor cardColor, int num) {
        super(cardColor);
        this.num = num;
    }

    @Override
    public void setNum(int num) {
    }

    @Override
    public int getNum() {
        return this.num;
    }

    @Override
    public boolean canBeSeen() {
        return true;
    }
}
