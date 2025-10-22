package com.hamusuke.numguesser.server.game.round.phase;

import com.hamusuke.numguesser.server.game.round.GameRound;
import com.hamusuke.numguesser.server.network.ServerPlayer;

public interface Cancellable {
    boolean isCancellable(final GameRound round, final ServerPlayer canceller);

    default void onPlayerCancel(final GameRound round, final ServerPlayer canceller) {
        if (this.isCancellable(round, canceller)) {
            round.prevPhase();
        }
    }
}
