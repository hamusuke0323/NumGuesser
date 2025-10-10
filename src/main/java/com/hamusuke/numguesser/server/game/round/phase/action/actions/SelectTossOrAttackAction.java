package com.hamusuke.numguesser.server.game.round.phase.action.actions;

public final class SelectTossOrAttackAction extends BooleanAction {
    public SelectTossOrAttackAction(boolean isToss) {
        super(isToss);
    }

    public boolean isToss() {
        return this.flag;
    }
}
