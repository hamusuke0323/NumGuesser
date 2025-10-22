package com.hamusuke.numguesser.server.game.round.phase.phases;

import com.hamusuke.numguesser.game.phase.PhaseType;
import com.hamusuke.numguesser.server.game.card.ServerCard;
import com.hamusuke.numguesser.server.game.event.events.PlayerSelectCardForAttackEvent;
import com.hamusuke.numguesser.server.game.round.GameRound;
import com.hamusuke.numguesser.server.game.round.phase.Actable;
import com.hamusuke.numguesser.server.game.round.phase.Cancellable;
import com.hamusuke.numguesser.server.game.round.phase.HasResult;
import com.hamusuke.numguesser.server.game.round.phase.ServerGamePhase;
import com.hamusuke.numguesser.server.game.round.phase.action.actions.SelectCardForAttackAction;
import com.hamusuke.numguesser.server.network.ServerPlayer;

public class SelectCardForAttackPhase implements ServerGamePhase, Actable<SelectCardForAttackAction>, Cancellable, HasResult<ServerCard> {
    private final boolean cancellable;
    private ServerCard selectedCard;

    public SelectCardForAttackPhase() {
        this(false);
    }

    public SelectCardForAttackPhase(final boolean cancellable) {
        this.cancellable = cancellable;
    }

    @Override
    public void onEnter(final GameRound round) {
        round.eventBus.post(new PlayerSelectCardForAttackEvent(round.getCurAttacker(), this.cancellable));
    }

    @Override
    public void onPlayerAction(final GameRound round, final ServerPlayer actor, final SelectCardForAttackAction action) {
        if (round.getCurAttacker() != actor) {
            return;
        }

        final var card = round.cardRegistry.getOwnedCardById(action.cardId());
        final var cardOwner = round.cardRegistry.getCardOwnerById(action.cardId());
        if (cardOwner != actor || card == null || card.isOpened()) {
            this.onEnter(round); // Try again
            return;
        }

        this.selectedCard = card;
        round.nextPhase();
    }

    @Override
    public void onPlayerLeftPost(GameRound round, ServerPlayer player) {
        if (round.getCurAttacker() != player) {
            return;
        }

        round.nextAttacker();
        this.selectedCard = null; // Cancel
        round.nextPhase();
    }

    @Override
    public PhaseType type() {
        return PhaseType.SELECT_ATTACK_CARD;
    }

    @Override
    public ServerCard getResult() {
        return this.selectedCard;
    }

    @Override
    public boolean isCancellable(final GameRound round, final ServerPlayer canceller) {
        return this.cancellable && round.getCurAttacker() == canceller;
    }
}
