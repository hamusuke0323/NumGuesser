package com.hamusuke.numguesser.client.game.card;

import com.hamusuke.numguesser.client.network.player.AbstractClientPlayer;
import com.hamusuke.numguesser.game.card.PlayerDeck;

import javax.swing.*;

public class ClientPlayerDeck extends PlayerDeck<AbstractClientCard, AbstractClientPlayer> {
    private final DefaultListModel<AbstractClientCard> model;

    public ClientPlayerDeck(final AbstractClientPlayer player) {
        super(player);
        this.model = new DefaultListModel<>();
    }

    public void tick() {
        this.cards.forEach(AbstractClientCard::tick);
    }

    @Override
    public int addCard(AbstractClientCard card) {
        this.addCard(this.cards.size(), card);
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
}
