package com.hamusuke.numguesser.server.game.event.events;

import com.hamusuke.numguesser.server.network.ServerPlayer;

public final class PlayerCardSelectEvent extends PlayerEvent {
    private final int cardId;

    public PlayerCardSelectEvent(ServerPlayer player, int cardId) {
        super(player);
        this.cardId = cardId;
    }

    public int getCardId() {
        return this.cardId;
    }
}
