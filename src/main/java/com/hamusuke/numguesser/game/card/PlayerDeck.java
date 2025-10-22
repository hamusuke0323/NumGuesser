package com.hamusuke.numguesser.game.card;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.hamusuke.numguesser.network.Player;

import java.util.List;

public abstract class PlayerDeck<C extends Card, P extends Player> {
    protected final P owner;
    protected final List<C> cards;

    public PlayerDeck(final P owner) {
        this.owner = owner;
        this.cards = Lists.newArrayList();
    }

    public int addCard(C card) {
        card.setOwner(this.owner);
        this.cards.add(card);
        this.sort();
        return this.cards.indexOf(card);
    }

    public void sort() {
        this.cards.sort(Card::compareTo);
    }

    public List<C> openAllCards() {
        var hasOpened = this.cards.stream()
                .filter(card -> !card.isOpened())
                .toList();
        hasOpened.forEach(Card::open);

        return hasOpened;
    }

    public boolean contains(C card) {
        return this.cards.contains(card);
    }

    public List<C> getCards() {
        return ImmutableList.copyOf(this.cards);
    }

    public P getOwner() {
        return this.owner;
    }
}
