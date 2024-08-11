package com.hamusuke.numguesser.client.game.card;

import com.hamusuke.numguesser.game.card.Card;

public abstract class AbstractClientCard extends Card {
    public AbstractClientCard(CardColor cardColor) {
        super(cardColor);
    }

    public abstract void setNum(int num);
}
