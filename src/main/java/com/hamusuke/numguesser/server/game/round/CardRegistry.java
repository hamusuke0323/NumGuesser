package com.hamusuke.numguesser.server.game.round;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.hamusuke.numguesser.game.card.Card;
import com.hamusuke.numguesser.server.game.card.ServerCard;
import com.hamusuke.numguesser.server.network.ServerPlayer;
import com.hamusuke.numguesser.util.Util;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class CardRegistry {
    private final List<Card> deck = Lists.newArrayList();
    private final Map<Integer, Card> ownCardIdMap = Maps.newConcurrentMap();
    private final Map<Integer, ServerPlayer> cardIdPlayerMap = Maps.newConcurrentMap();

    public CardRegistry(final Random random) {
        this.newDeck();
        this.randomizeCardIds(random);
    }

    private void newDeck() {
        this.deck.clear();
        for (var color : Card.CardColor.values()) {
            for (int i = 0; i < 12; i++) {
                this.deck.add(new ServerCard(color, i));
            }
        }
    }

    private void randomizeCardIds(final Random random) {
        final var uniqueIdCounter = new AtomicInteger(random.nextInt(8));
        Collections.shuffle(this.deck, random);
        this.deck.forEach(card -> card.setId(uniqueIdCounter.getAndIncrement()));
    }

    public Card pullBy(final ServerPlayer player) {
        final var card = this.pull();
        this.own(player, card);
        return card;
    }

    public Card pull() {
        return this.deck.remove(0);
    }

    public boolean own(final ServerPlayer player, final Card card) {
        if (this.ownCardIdMap.containsKey(card.getId())) {
            return false;
        }

        this.ownCardIdMap.put(card.getId(), card);
        this.cardIdPlayerMap.put(card.getId(), player);
        return true;
    }

    public boolean isCardOwnedBy(final ServerPlayer player, final Card card) {
        return this.getCardOwnerById(card.getId()) == player;
    }

    public Card peek(final Random random) {
        return Util.chooseRandom(this.deck, random);
    }

    public void shuffle(final Random random) {
        Collections.shuffle(this.deck, random);
    }

    public boolean isEmpty() {
        return this.deck.isEmpty();
    }

    public Card getOwnedCardById(final int id) {
        return this.ownCardIdMap.get(id);
    }

    public ServerPlayer getCardOwnerById(final int id) {
        return this.cardIdPlayerMap.get(id);
    }
}
