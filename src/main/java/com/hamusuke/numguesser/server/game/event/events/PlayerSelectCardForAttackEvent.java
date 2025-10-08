package com.hamusuke.numguesser.server.game.event.events;

import com.hamusuke.numguesser.server.network.ServerPlayer;

public final class PlayerSelectCardForAttackEvent extends PlayerEvent {
    private final boolean isCancellable;

    public PlayerSelectCardForAttackEvent(ServerPlayer player, boolean isCancellable) {
        super(player);
        this.isCancellable = isCancellable;
    }

    public boolean isCancellable() {
        return this.isCancellable;
    }
}
