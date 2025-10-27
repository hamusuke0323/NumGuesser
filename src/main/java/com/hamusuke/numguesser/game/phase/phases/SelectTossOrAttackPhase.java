package com.hamusuke.numguesser.game.phase.phases;

import com.hamusuke.numguesser.game.phase.GamePhase;
import com.hamusuke.numguesser.game.phase.PhaseType;

public abstract class SelectTossOrAttackPhase implements GamePhase {
    @Override
    public PhaseType type() {
        return PhaseType.SELECT_TOSS_OR_ATTACK;
    }
}
