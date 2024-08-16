package com.hamusuke.numguesser.client.game.card;

import com.hamusuke.numguesser.client.network.player.AbstractClientPlayer;
import com.hamusuke.numguesser.game.card.Card;
import com.hamusuke.numguesser.game.card.PlayerDeck;

import javax.swing.*;

public class ClientPlayerDeck extends PlayerDeck {
    private final AbstractClientPlayer player;
    private final DefaultListModel<AbstractClientCard> model;

    public ClientPlayerDeck(final AbstractClientPlayer player) {
        this.player = player;
        this.model = new DefaultListModel<>();
    }

    public void tick() {
        this.cards.forEach(card -> ((AbstractClientCard) card).tick());
    }

    @Override
    public int addCard(Card card) {
        this.addCard(this.cards.size(), (AbstractClientCard) card);
        return this.cards.size();
    }

    public void addCard(int index, AbstractClientCard card) {
        this.cards.add(index, card);
        this.model.add(index, card);
    }

    @Override
    public void sort() {
    }

    public DefaultListModel<AbstractClientCard> getCardModel() {
        return this.model;
    }

    @Override
    public AbstractClientPlayer getOwner() {
        return this.player;
    }
}
