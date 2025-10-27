package com.hamusuke.numguesser.server.game.round.phase.phases;

import com.hamusuke.numguesser.game.card.Card;
import com.hamusuke.numguesser.game.phase.PhaseType;
import com.hamusuke.numguesser.server.game.card.ServerCard;
import com.hamusuke.numguesser.server.game.round.GameRound;
import com.hamusuke.numguesser.server.network.ServerPlayer;

public class ServerPairAttackPhase extends ServerAttackPhase {
    public ServerPairAttackPhase(final boolean cancellable, final ServerCard cardForAttack) {
        super(cancellable, cardForAttack);
    }

    @Override
    protected boolean canAttack(final GameRound round, final ServerPlayer attacker, final Card card) {
        final var cardOwner = round.cardRegistry.getCardOwnerById(card.getId());
        return super.canAttack(round, attacker, card) && cardOwner.getPairColor() != round.getCurAttacker().getPairColor();
    }

    @Override
    public PhaseType type() {
        return PhaseType.PAIR_ATTACK;
    }
}
