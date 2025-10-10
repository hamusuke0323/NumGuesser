package com.hamusuke.numguesser.server.game.event.events;

import com.hamusuke.numguesser.server.network.ServerPlayer;

public final class PlayerSelectTossOrAttackEvent extends PlayerEvent {
    public PlayerSelectTossOrAttackEvent(ServerPlayer player) {
        super(player);
    }
}
