package com.hamusuke.numguesser.server.game.round.phase.action.actions;

public abstract sealed class BooleanAction permits ContinueOrStayAction, SelectTossOrAttackAction {
    protected final boolean flag;

    protected BooleanAction(final boolean flag) {
        this.flag = flag;
    }
}
