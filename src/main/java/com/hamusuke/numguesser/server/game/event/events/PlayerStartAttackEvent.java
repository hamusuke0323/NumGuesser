package com.hamusuke.numguesser.server.game.event.events;

import com.hamusuke.numguesser.server.game.card.ServerCard;
import com.hamusuke.numguesser.server.network.ServerPlayer;

public final class PlayerStartAttackEvent extends PlayerEvent {
    private final ServerCard card;
    private final boolean isCancellable;

    public PlayerStartAttackEvent(ServerPlayer player, ServerCard card, boolean isCancellable) {
        super(player);
        this.card = card;
        this.isCancellable = isCancellable;
    }

    public ServerCard getCard() {
        return this.card;
    }

    public boolean isCancellable() {
        return this.isCancellable;
    }
}
