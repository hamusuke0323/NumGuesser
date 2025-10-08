package com.hamusuke.numguesser.server.game.event.events;

import com.hamusuke.numguesser.server.network.ServerPlayer;

public final class PlayerDeckSyncEvent extends PlayerEvent {
    public PlayerDeckSyncEvent(final ServerPlayer player) {
        super(player);
    }
}
