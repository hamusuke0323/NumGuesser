package com.hamusuke.numguesser.server.game.round.phase.action.actions;

public final class ContinueOrStayAction extends BooleanAction {
    public ContinueOrStayAction(final boolean continueAttacking) {
        super(continueAttacking);
    }

    public boolean continueAttacking() {
        return this.flag;
    }
}
