package com.hamusuke.numguesser.server.game.round.phase.phases;

import com.hamusuke.numguesser.game.phase.PhaseType;
import com.hamusuke.numguesser.server.game.round.GameRound;
import com.hamusuke.numguesser.server.game.round.PairGameRound;
import com.hamusuke.numguesser.server.game.round.phase.HasResult;
import com.hamusuke.numguesser.server.game.round.phase.ServerGamePhase;

public class ServerCheckBuddyAlivePhase implements ServerGamePhase, HasResult<Boolean> {
    private boolean isAlive;

    @Override
    public void onEnter(final GameRound round) {
        this.isAlive = !((PairGameRound) round).pairRegistry.getBuddyFor(round.getCurAttacker()).isDefeated();
        round.nextPhase();
    }

    @Override
    public PhaseType type() {
        return PhaseType.CHECK_BUDDY_ALIVE;
    }

    @Override
    public Boolean getResult() {
        return this.isAlive;
    }
}
