package com.hamusuke.numguesser.game.card;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.hamusuke.numguesser.network.Player;

import java.util.List;

public abstract class PlayerDeck {
    protected final List<Card> cards;

    public PlayerDeck() {
        this.cards = Lists.newArrayList();
    }

    public int addCard(Card card) {
        this.cards.add(card);
        this.sort();
        return this.cards.indexOf(card);
    }

    public void sort() {
        this.cards.sort(Card::compareTo);
    }

    public List<Card> openAllCards() {
        var hasOpened = this.cards.stream()
                .filter(card -> !card.isOpened())
                .toList();
        hasOpened.forEach(Card::open);

        return hasOpened;
    }

    public boolean contains(Card card) {
        return this.cards.contains(card);
    }

    public List<? extends Card> getCards() {
        return ImmutableList.copyOf(this.cards);
    }

    public abstract Player getOwner();
}
