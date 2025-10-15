package com.hamusuke.numguesser.server.game.round.phase;

import com.hamusuke.numguesser.server.game.round.GameRound;
import com.hamusuke.numguesser.server.network.ServerPlayer;

public interface CancellableGamePhase<R> extends GamePhase<R> {
    boolean isCancellable(final GameRound round, final ServerPlayer canceller);

    default void onPlayerCancel(final GameRound round, final ServerPlayer canceller) {
        if (this.isCancellable(round, canceller)) {
            round.prevPhase();
        }
    }
}
