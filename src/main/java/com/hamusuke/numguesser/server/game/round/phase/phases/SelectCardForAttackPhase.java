package com.hamusuke.numguesser.server.game.round.phase.phases;

import com.hamusuke.numguesser.game.card.Card;
import com.hamusuke.numguesser.server.game.event.events.PlayerSelectCardForAttackEvent;
import com.hamusuke.numguesser.server.game.round.GameRound;
import com.hamusuke.numguesser.server.game.round.phase.ActableGamePhase;
import com.hamusuke.numguesser.server.game.round.phase.CancellableGamePhase;
import com.hamusuke.numguesser.server.game.round.phase.action.actions.SelectCardForAttackAction;
import com.hamusuke.numguesser.server.network.ServerPlayer;

public class SelectCardForAttackPhase implements ActableGamePhase<SelectCardForAttackAction, Card>, CancellableGamePhase<Card> {
    private final boolean cancellable;
    private Card selectedCard;

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
    public Card getResult() {
        return this.selectedCard;
    }

    @Override
    public boolean isCancellable() {
        return this.cancellable;
    }
}
