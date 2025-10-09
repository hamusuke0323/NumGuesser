package com.hamusuke.numguesser.server.game.round.phase.action.actions;

public sealed interface AttackActions permits AttackActions.DoAttack, AttackActions.Select {
    record Select(int cardId) implements AttackActions {
    }

    record DoAttack(int cardId, int numExpected) implements AttackActions {
    }
}
