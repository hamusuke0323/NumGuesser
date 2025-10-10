package com.hamusuke.numguesser.server.game.event.events;

import com.hamusuke.numguesser.server.network.ServerPlayer;

public abstract sealed class PlayerEvent implements GameEvent permits PlayerCardSelectEvent, PlayerDeckSyncEvent, PlayerNewCardAddEvent, PlayerSelectCardForAttackEvent, PlayerSelectCardForTossEvent, PlayerSelectTossOrAttackEvent, PlayerStartAttackEvent {
    protected final ServerPlayer player;

    public PlayerEvent(final ServerPlayer player) {
        this.player = player;
    }

    public ServerPlayer getPlayer() {
        return this.player;
    }
}
