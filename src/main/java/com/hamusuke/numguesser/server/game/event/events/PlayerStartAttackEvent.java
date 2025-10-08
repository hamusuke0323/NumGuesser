package com.hamusuke.numguesser.server.game.event.events;

import com.hamusuke.numguesser.game.card.Card;
import com.hamusuke.numguesser.server.network.ServerPlayer;

public final class PlayerStartAttackEvent extends PlayerEvent {
    private final Card card;
    private final boolean isCancellable;

    public PlayerStartAttackEvent(ServerPlayer player, Card card, boolean isCancellable) {
        super(player);
        this.card = card;
        this.isCancellable = isCancellable;
    }

    public Card getCard() {
        return this.card;
    }

    public boolean isCancellable() {
        return this.isCancellable;
    }
}
