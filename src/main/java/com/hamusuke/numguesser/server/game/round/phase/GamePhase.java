package com.hamusuke.numguesser.server.game.round.phase;

import com.hamusuke.numguesser.server.game.round.GameRound;

public interface GamePhase<C> {
    void onEnter(final GameRound round);

    void onPlayerAction(final GameRound round, final C actionContext);

    void onExit(final GameRound round);
}
