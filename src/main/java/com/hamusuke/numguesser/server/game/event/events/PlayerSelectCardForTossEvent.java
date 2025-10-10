package com.hamusuke.numguesser.server.game.event.events;

import com.hamusuke.numguesser.server.network.ServerPlayer;

public final class PlayerSelectCardForTossEvent extends PlayerEvent {
    public PlayerSelectCardForTossEvent(ServerPlayer player) {
        super(player);
    }
}
