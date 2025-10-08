package com.hamusuke.numguesser.server.game.round;

import com.hamusuke.numguesser.game.card.Card;
import com.hamusuke.numguesser.game.pair.PlayerPair;
import com.hamusuke.numguesser.network.Player;
import com.hamusuke.numguesser.network.protocol.packet.common.clientbound.ChatNotify;
import com.hamusuke.numguesser.network.protocol.packet.play.clientbound.TossNotify;
import com.hamusuke.numguesser.server.game.PairPlayGame;
import com.hamusuke.numguesser.server.game.event.events.GameMessageEvent;
import com.hamusuke.numguesser.server.game.event.events.PlayerSelectCardForTossEvent;
import com.hamusuke.numguesser.server.game.event.events.PlayerSelectTossOrAttackEvent;
import com.hamusuke.numguesser.server.game.pair.ServerPlayerPairRegistry;
import com.hamusuke.numguesser.server.network.ServerPlayer;

import javax.annotation.Nullable;
import java.util.List;

public class PairGameRound extends GameRound {
    private final ServerPlayerPairRegistry pairRegistry;

    public PairGameRound(PairPlayGame game, List<ServerPlayer> players) {
        super(game, players);
        this.pairRegistry = game.getPairRegistry();
    }

    protected PairGameRound(final PairGameRound old) {
        super(old);
        this.pairRegistry = old.pairRegistry;
    }

    @Override
    protected void startAttacking() {
        // If the buddy for the attacker is defeated, toss is not allowed.
        if (this.pairRegistry.getBuddyFor(this.curAttacker).isDefeated()) {
            this.selectCardForAttack();
            return;
        }

        this.selectTossOrAttack();
    }

    protected void selectTossOrAttack() {
        this.gameState = GameState.SELECTING_TOSS_OR_ATTACKING;
        this.eventBus.post(new PlayerSelectTossOrAttackEvent(this.curAttacker));
    }

    public void onTossSelected(ServerPlayer selector) {
        if (this.curAttacker != selector || this.gameState != GameState.SELECTING_TOSS_OR_ATTACKING) {
            return;
        }

        var buddy = this.pairRegistry.getBuddyFor(this.curAttacker);
        if (buddy.isDefeated()) { // hey bro, i am already defeated, lol
            this.selectCardForAttack();
            return;
        }

        this.gameState = GameState.TOSSING;
        this.eventBus.post(new PlayerSelectCardForTossEvent(buddy));
    }

    public void onToss(ServerPlayer tosser, int cardId) {
        var cardHolder = this.cardRegistry.getCardOwnerById(cardId);
        var card = this.cardRegistry.getOwnedCardById(cardId); // card == null should never happen
        if (this.gameState != GameState.TOSSING || cardHolder != tosser || card == null) {
            return;
        }

        this.curAttacker.sendPacket(new TossNotify(card.toSerializer()));
        this.curAttacker.sendPacket(new ChatNotify("味方があなたにトスしました"));
        this.selectCardForAttack();
    }

    public void onAttackSelected(ServerPlayer selector) {
        if (this.curAttacker != selector || this.gameState != GameState.SELECTING_TOSS_OR_ATTACKING) {
            return;
        }

        this.selectCardForAttack(CancelOperation.BACK_TO_SELECTING_TOSS_OR_ATTACKING); // cancel operation for misclick.
    }

    @Override
    protected boolean canAttack(Card card) {
        var cardOwner = this.cardRegistry.getCardOwnerById(card.getId());
        return super.canAttack(card) && cardOwner.getPairColor() != this.curAttacker.getPairColor();
    }

    @Override
    protected void ownCard(ServerPlayer player, Card card) {
    }

    @Override
    public void onCancelCommand(ServerPlayer canceller) {
        if (this.curAttacker == canceller && this.gameState == GameState.SELECTING_CARD_FOR_ATTACKING && this.cancelOperation == CancelOperation.BACK_TO_SELECTING_TOSS_OR_ATTACKING) {
            this.selectTossOrAttack();
            return;
        }

        super.onCancelCommand(canceller);
    }

    @Override
    protected boolean arePlayersDefeatedBy(@Nullable ServerPlayer player) {
        return this.players.stream()
                .filter(sp -> !sp.equals(player) && !sp.equals(this.pairRegistry.getBuddyFor(player)))
                .allMatch(ServerPlayer::isDefeated);
    }

    @Override
    protected void giveTipToRoundWinner() {
        if (this.winner == null) {
            return;
        }

        var pair = this.pairRegistry.get(this.winner);
        var buddy = pair.getBuddyFor(this.winner);
        int point = this.winner.getDeck().getCards().stream()
                .mapToInt(Card::getPoint)
                .sum();

        point += buddy.getDeck().getCards().stream()
                .mapToInt(Card::getPoint)
                .sum();

        this.winner.addTipPoint(point);
        buddy.addTipPoint(point);

        // defeated pair give tip to won pair.
        for (var player : this.players) {
            if (player == this.winner || player == buddy) {
                continue;
            }

            player.subTipPoint(point);
        }
    }

    @Override
    protected void showWonMessage() {
        int bluePairPoints = this.pairRegistry.getPlayers(PlayerPair.PairColor.BLUE).stream()
                .mapToInt(Player::getTipPoint)
                .sum();
        int redPairPoints = this.pairRegistry.getPlayers(PlayerPair.PairColor.RED).stream()
                .mapToInt(Player::getTipPoint)
                .sum();

        if (bluePairPoints == redPairPoints) {
            this.eventBus.post(new GameMessageEvent("どちらのペアも得点が同じなのでドローです"));
            return;
        }

        final var wonPair = this.pairRegistry.get(bluePairPoints > redPairPoints ? PlayerPair.PairColor.BLUE : PlayerPair.PairColor.RED);
        this.eventBus.post(new GameMessageEvent("合計" + Math.max(bluePairPoints, redPairPoints) + "点で " + wonPair.left().getDisplayName() + " と " + wonPair.right().getDisplayName() + " が勝利しました"));
    }

    @Override
    protected int getGivenCardNumPerPlayer() {
        return 6;
    }

    @Override
    public GameRound newRound() {
        return new PairGameRound(this);
    }
}
