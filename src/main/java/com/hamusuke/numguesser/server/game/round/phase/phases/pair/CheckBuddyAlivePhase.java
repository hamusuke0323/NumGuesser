package com.hamusuke.numguesser.server.game.round.phase.phases.pair;

import com.hamusuke.numguesser.server.game.round.GameRound;
import com.hamusuke.numguesser.server.game.round.PairGameRound;
import com.hamusuke.numguesser.server.game.round.phase.GamePhase;

public class CheckBuddyAlivePhase implements GamePhase<Boolean> {
    private boolean isAlive;

    @Override
    public void onEnter(final GameRound round) {
        this.isAlive = !((PairGameRound) round).pairRegistry.getBuddyFor(round.getCurAttacker()).isDefeated();
        round.nextPhase();
    }

    @Override
    public Boolean getResult() {
        return this.isAlive;
    }
}
