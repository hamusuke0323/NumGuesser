package com.hamusuke.numguesser.client.game.card;

public class RemoteCard extends AbstractClientCard {
    private int num = UNKNOWN;

    public RemoteCard(CardColor cardColor) {
        super(cardColor);
    }

    @Override
    public void setNum(int num) {
        this.num = num;
    }

    @Override
    public int getNum() {
        return this.num;
    }
}
