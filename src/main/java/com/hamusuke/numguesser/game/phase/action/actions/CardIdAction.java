package com.hamusuke.numguesser.game.phase.action.actions;

public abstract sealed class CardIdAction permits AttackActions, SelectCardForAttackAction, TossAction {
    protected final int cardId;

    protected CardIdAction(final int cardId) {
        this.cardId = cardId;
    }

    public int cardId() {
        return this.cardId;
    }
}
