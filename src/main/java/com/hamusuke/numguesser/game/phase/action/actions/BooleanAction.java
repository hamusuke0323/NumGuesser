package com.hamusuke.numguesser.game.phase.action.actions;

public abstract sealed class BooleanAction permits ContinueOrStayAction, SelectTossOrAttackAction {
    protected final boolean flag;

    protected BooleanAction(final boolean flag) {
        this.flag = flag;
    }
}
