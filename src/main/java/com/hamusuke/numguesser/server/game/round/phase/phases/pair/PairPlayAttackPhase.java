package com.hamusuke.numguesser.server.game.round.phase.phases.pair;

import com.hamusuke.numguesser.game.card.Card;
import com.hamusuke.numguesser.server.game.round.GameRound;
import com.hamusuke.numguesser.server.game.round.phase.phases.AttackPhase;
import com.hamusuke.numguesser.server.network.ServerPlayer;

public class PairPlayAttackPhase extends AttackPhase {
    public PairPlayAttackPhase(final Card cardForAttack) {
        super(cardForAttack);
    }

    public PairPlayAttackPhase(final boolean cancellable, final Card cardForAttack) {
        super(cancellable, cardForAttack);
    }

    @Override
    protected boolean canAttack(final GameRound round, final ServerPlayer attacker, final Card card) {
        final var cardOwner = round.cardRegistry.getCardOwnerById(card.getId());
        return super.canAttack(round, attacker, card) && cardOwner.getPairColor() != round.getCurAttacker().getPairColor();
    }
}
