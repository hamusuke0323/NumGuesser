package com.hamusuke.numguesser.server.game.round.phase;

import com.hamusuke.numguesser.server.game.round.GameRound;
import com.hamusuke.numguesser.server.network.ServerPlayer;

public interface ActableGamePhase<A, R> extends GamePhase<R> {
    void onPlayerAction(final GameRound round, final ServerPlayer actor, final A action);
}
