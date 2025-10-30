package com.hamusuke.numguesser.game.phase.phases;

import com.hamusuke.numguesser.game.phase.GamePhase;
import com.hamusuke.numguesser.game.phase.PhaseType;

public abstract class TossPhase implements GamePhase {
    @Override
    public PhaseType type() {
        return PhaseType.TOSS;
    }
}
