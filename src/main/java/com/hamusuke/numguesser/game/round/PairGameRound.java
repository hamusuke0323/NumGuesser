package com.hamusuke.numguesser.game.round;

import com.hamusuke.numguesser.game.card.Card;
import com.hamusuke.numguesser.game.mode.PairPlayGameMode;
import com.hamusuke.numguesser.network.Player;
import com.hamusuke.numguesser.network.protocol.packet.common.clientbound.ChatNotify;
import com.hamusuke.numguesser.network.protocol.packet.play.clientbound.*;
import com.hamusuke.numguesser.server.game.ServerPlayerPair;
import com.hamusuke.numguesser.server.network.ServerPlayer;

import javax.annotation.Nullable;
import java.util.List;

public class PairGameRound extends GameRound {
    private final ServerPlayerPair bluePair;
    private final ServerPlayerPair redPair;

    public PairGameRound(PairPlayGameMode game, List<ServerPlayer> players, @Nullable ServerPlayer parent) {
        super(game, players, parent);
        this.bluePair = game.getBluePair();
        this.redPair = game.getRedPair();
    }

    @Override
    protected void startAttacking() {
        // If the buddy for the attacker is defeated, toss is not allowed.
        if (this.getBuddyFor(this.curAttacker).isDefeated()) {
            this.selectCardForAttack();
            return;
        }

        this.selectTossOrAttack();
    }

    protected void selectTossOrAttack() {
        this.gameState = GameState.SELECTING_TOSS_OR_ATTACKING;
        this.curAttacker.sendPacket(TossOrAttackSelectionNotify.INSTANCE);
        this.sendPacketToOthersInGame(this.curAttacker, new RemotePlayerSelectTossOrAttackNotify(this.curAttacker.getId()));
    }

    public void onTossSelected(ServerPlayer selector) {
        if (this.curAttacker != selector || this.gameState != GameState.SELECTING_TOSS_OR_ATTACKING) {
            return;
        }

        var buddy = this.getBuddyFor(this.curAttacker);
        if (buddy.isDefeated()) { // hey bro, i am already defeated, lol
            this.selectCardForAttack();
            return;
        }

        this.gameState = GameState.TOSSING;
        buddy.sendPacket(TossReq.INSTANCE);
        this.sendPacketToOthersInGame(buddy, new RemotePlayerSelectCardForTossNotify(buddy.getId()));
    }

    public void onToss(ServerPlayer tosser, int cardId) {
        var cardHolder = this.cardIdPlayerMap.get(cardId);
        var card = this.ownCardIdMap.get(cardId); // card == null should never happen
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

    protected final ServerPlayerPair getPairFor(ServerPlayer player) {
        return switch (player.getPairColor()) {
            case BLUE -> this.bluePair;
            case RED -> this.redPair;
        };
    }

    protected final ServerPlayer getBuddyFor(ServerPlayer oneOfPair) {
        return this.getPairFor(oneOfPair).getBuddyFor(oneOfPair);
    }

    @Override
    protected boolean canAttack(Card card) {
        var cardOwner = this.cardIdPlayerMap.get(card.getId());
        return super.canAttack(card) && cardOwner.getPairColor() != this.curAttacker.getPairColor();
    }

    @Override
    protected void ownCard(ServerPlayer player, Card card) {
    }

    @Override
    protected void setSeatingArrangement() {
        if (!this.seatingArrangement.isEmpty()) {
            return;
        }

        super.setSeatingArrangement(); // invoke super method to replace them after
        int startIndex = this.random.nextInt(4); // first seat is selected randomly.

        // seating permutation is like this:
        // one of blue pair, one of red pair, the other of blue pair, and the other of red pair.
        for (int i = 0; i < this.players.size(); i++) {
            int seatIndex = (i + startIndex) % this.players.size();
            var pair = i % 2 == 0 ? this.bluePair : this.redPair;
            this.seatingArrangement.set(seatIndex, (i < 2 ? pair.left() : pair.right()).getId());
        }
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
                .filter(sp -> !sp.equals(player) && !sp.equals(this.getBuddyFor(player)))
                .allMatch(ServerPlayer::isDefeated);
    }

    @Override
    protected void giveTipToRoundWinner() {
        if (this.winner == null) {
            return;
        }

        var pair = this.getPairFor(this.winner);
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
        int bluePairPoints = this.bluePair.getPlayers().stream().mapToInt(Player::getTipPoint).sum();
        int redPairPoints = this.redPair.getPlayers().stream().mapToInt(Player::getTipPoint).sum();

        if (bluePairPoints == redPairPoints) {
            this.sendPacketToAllInGame(new ChatNotify("どちらのペアも得点が同じなのでドローです"));
            return;
        }

        var wonPair = bluePairPoints > redPairPoints ? this.bluePair : this.redPair;
        this.sendPacketToAllInGame(new ChatNotify("合計" + Math.max(bluePairPoints, redPairPoints) + "点で " + wonPair.left().getDisplayName() + " と " + wonPair.right().getDisplayName() + " が勝利しました"));
    }

    @Override
    protected int getGivenCardNumPerPlayer() {
        return 6;
    }

    @Override
    public GameRound newRound() {
        var game = new PairGameRound((PairPlayGameMode) this.game, this.players, this.nextParent());
        game.pulledCardMapForDecidingParent.putAll(this.pulledCardMapForDecidingParent);
        game.seatingArrangement.addAll(this.seatingArrangement);
        return game;
    }
}
