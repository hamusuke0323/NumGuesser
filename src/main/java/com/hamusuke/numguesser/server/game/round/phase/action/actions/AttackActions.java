package com.hamusuke.numguesser.server.game.round.phase.action.actions;

public sealed abstract class AttackActions extends CardIdAction permits AttackActions.DoAttack, AttackActions.Select {
    protected AttackActions(final int cardId) {
        super(cardId);
    }

    public static final class Select extends AttackActions {
        public Select(final int cardId) {
            super(cardId);
        }
    }

    public static final class DoAttack extends AttackActions {
        private final int numExpected;

        public DoAttack(final int cardId, final int numExpected) {
            super(cardId);
            this.numExpected = numExpected;
        }

        public int numExpected() {
            return this.numExpected;
        }
    }
}
