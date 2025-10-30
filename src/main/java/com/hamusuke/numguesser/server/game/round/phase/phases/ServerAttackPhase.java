package com.hamusuke.numguesser.server.game.round.phase.phases;

import com.hamusuke.numguesser.game.Game;
import com.hamusuke.numguesser.game.card.Card;
import com.hamusuke.numguesser.game.phase.PhaseType;
import com.hamusuke.numguesser.game.phase.action.actions.AttackActions;
import com.hamusuke.numguesser.game.phase.phases.AttackPhase;
import com.hamusuke.numguesser.server.game.card.ServerCard;
import com.hamusuke.numguesser.server.game.event.events.CardOpenEvent;
import com.hamusuke.numguesser.server.game.event.events.GameMessageEvent;
import com.hamusuke.numguesser.server.game.event.events.PlayerCardSelectEvent;
import com.hamusuke.numguesser.server.game.round.GameRound;
import com.hamusuke.numguesser.server.game.round.phase.Actable;
import com.hamusuke.numguesser.server.game.round.phase.Cancellable;
import com.hamusuke.numguesser.server.game.round.phase.HasResult;
import com.hamusuke.numguesser.server.game.round.phase.ServerGamePhase;
import com.hamusuke.numguesser.server.network.ServerPlayer;

public class ServerAttackPhase extends AttackPhase implements ServerGamePhase, Actable<AttackActions>, Cancellable, HasResult<ServerAttackPhase.Result> {
    private final boolean cancellable;
    private final ServerCard cardForAttacking;
    private Result result;

    public ServerAttackPhase(final ServerCard cardForAttacking) {
        this(false, cardForAttacking);
    }

    public ServerAttackPhase(final boolean cancellable, final ServerCard cardForAttacking) {
        this.cancellable = cancellable;
        this.cardForAttacking = cardForAttacking;
    }

    @Override
    public void syncGameData(final GameRound round) {
        round.game.setSyncedData(Game.CANCELLABLE, this.cancellable);
        round.game.setSyncedData(Game.ATTACK_CARD, new SyncedAttackData(round.getCurAttacker().getId(), this.cardForAttacking.toSerializer()));
    }

    @Override
    public void onPlayerAction(final GameRound round, final ServerPlayer actor, final AttackActions action) {
        switch (action) {
            case AttackActions.Select select -> {
                if (round.getCurAttacker() != actor) {
                    return;
                }

                final var cardOwner = round.cardRegistry.getCardOwnerById(select.cardId());
                if (actor != cardOwner) { // the attacker must select the others' cards.
                    round.eventBus.post(new PlayerCardSelectEvent(actor, select.cardId()));
                }
            }
            case AttackActions.DoAttack doAttack -> {
                if (round.getCurAttacker() != actor) {
                    return;
                }

                final var card = round.cardRegistry.getOwnedCardById(doAttack.cardId());
                if (card == null || card.isOpened() || !this.canAttack(round, actor, card)) {
                    return;
                }

                round.eventBus.post(new GameMessageEvent("アタック: " +
                        actor.getDisplayName() + "が" + doAttack.numExpected() + "で" +
                        round.cardRegistry.getCardOwnerById(doAttack.cardId()).getDisplayName() +
                        "にアタックしました"));

                if (card.getNum() == doAttack.numExpected()) {
                    this.onAttackSucceeded(round, card);
                } else {
                    this.onAttackFailed(round, card);
                }
            }
        }
    }

    protected void onAttackSucceeded(final GameRound round, final ServerCard card) {
        card.open();
        round.eventBus.post(new CardOpenEvent(card));
        round.eventBus.post(new GameMessageEvent("アタック成功です！"));
        this.giveTipToAttacker(round, card);

        if (this.shouldEndRound(round, card)) {
            round.ownCard(round.getCurAttacker(), this.cardForAttacking);
            round.setWinner(round.getCurAttacker());
            round.endRound();
            return;
        }

        this.result = new Result.Success(this.cardForAttacking);
        round.nextPhase();
    }

    protected void onAttackFailed(final GameRound round, final Card closedCard) {
        round.ownCard(round.getCurAttacker(), this.cardForAttacking);
        this.cardForAttacking.open();
        round.eventBus.post(new CardOpenEvent(this.cardForAttacking));
        round.eventBus.post(new GameMessageEvent("アタック失敗です"));

        final var closedCardOwner = round.cardRegistry.getCardOwnerById(closedCard.getId());
        if (this.shouldEndRound(round, this.cardForAttacking) || round.arePlayersDefeatedBy(closedCardOwner)) {
            // when all players excluding the closed card owner are defeated, end the round.
            round.setWinner(closedCardOwner);
            round.endRound();
            return;
        }

        round.nextAttacker();
        this.result = new Result.Failure();
        round.nextPhase();
    }

    protected void giveTipToAttacker(final GameRound round, final Card openedCard) {
        final var cardOwner = round.cardRegistry.getCardOwnerById(openedCard.getId());
        if (cardOwner != null) {
            cardOwner.subTipPoint(openedCard.getPoint());
        }

        round.getCurAttacker().addTipPoint(openedCard.getPoint());
    }

    protected boolean shouldEndRound(final GameRound round, final Card openedCard) {
        final var player = round.cardRegistry.getCardOwnerById(openedCard.getId());
        if (player == null) {
            round.eventBus.post(new GameMessageEvent(new NullPointerException("player is null").toString()));
            round.eventBus.post(new GameMessageEvent("エラーが発生したのでラウンドを終了します"));
            return true;
        }

        if (player.getDeck().getCards().stream().allMatch(Card::isOpened)) {
            player.setIsDefeated(true);
        }

        return round.arePlayersDefeatedBy(round.getCurAttacker());
    }

    protected boolean canAttack(final GameRound round, final ServerPlayer attacker, final Card card) {
        return !round.cardRegistry.isCardOwnedBy(attacker, card);
    }

    @Override
    public void onPlayerLeftPost(final GameRound round, final ServerPlayer player) {
        if (round.getCurAttacker() != player) {
            return;
        }

        this.cardForAttacking.open();
        round.ownCard(player, this.cardForAttacking);
        round.nextAttacker();
        round.eventBus.post(new CardOpenEvent(this.cardForAttacking));
        this.result = new Result.Failure();
        round.nextPhase();
    }

    @Override
    public PhaseType type() {
        return PhaseType.ATTACK;
    }

    @Override
    public Result getResult() {
        return this.result;
    }

    @Override
    public boolean isCancellable(final GameRound round, final ServerPlayer canceller) {
        return this.cancellable && round.getCurAttacker() == canceller;
    }

    public sealed interface Result permits Result.Failure, Result.Success {
        record Success(ServerCard cardForAttack) implements Result {
        }

        record Failure() implements Result {
        }
    }
}
