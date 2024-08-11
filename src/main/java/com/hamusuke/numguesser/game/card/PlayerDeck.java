package com.hamusuke.numguesser.game.card;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.hamusuke.numguesser.network.Player;

import java.util.List;

public abstract class PlayerDeck {
    private final List<Card> cards;

    public PlayerDeck() {
        this.cards = Lists.newArrayList();
    }

    public void addCard(Card card) {
        this.cards.add(card);
    }

    public void sort() {
        this.cards.sort(Card::compareTo);
    }

    public void removeCard(Card card) {
        this.cards.remove(card);
    }

    public void openCard(Card card) {
        this.cards.stream()
                .filter(card1 -> card1.equals(card))
                .findFirst()
                .ifPresent(Card::open);
    }

    public List<Card> getCards() {
        return ImmutableList.copyOf(this.cards);
    }

    public abstract Player getOwner();
}
