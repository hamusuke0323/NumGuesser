package com.hamusuke.numguesser.game.phase.phases;

import com.hamusuke.numguesser.game.phase.GamePhase;
import com.hamusuke.numguesser.game.phase.PhaseType;

public abstract class PairMakingPhase implements GamePhase {
    @Override
    public PhaseType type() {
        return PhaseType.PAIR_MAKING;
    }
}
