package com.hamusuke.numguesser.server.game.event.events;

import com.hamusuke.numguesser.game.card.Card;
import com.hamusuke.numguesser.server.network.ServerPlayer;

public final class PlayerNewCardAddEvent extends PlayerEvent {
    private final int index;
    private final Card card;

    public PlayerNewCardAddEvent(ServerPlayer player, int index, Card card) {
        super(player);
        this.index = index;
        this.card = card;
    }

    public int getIndex() {
        return this.index;
    }

    public Card getCard() {
        return this.card;
    }
}
