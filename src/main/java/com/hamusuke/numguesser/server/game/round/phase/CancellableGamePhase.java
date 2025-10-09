package com.hamusuke.numguesser.server.game.round.phase;

import com.hamusuke.numguesser.server.game.round.GameRound;
import com.hamusuke.numguesser.server.network.ServerPlayer;

public interface CancellableGamePhase<R> extends GamePhase<R> {
    boolean isCancellable();

    default void onPlayerCancel(final GameRound round, final ServerPlayer player) {
        if (this.isCancellable()) {
            round.prevPhase();
        }
    }
}
