package com.hamusuke.numguesser.server.game.round;

import com.hamusuke.numguesser.game.card.Card;
import com.hamusuke.numguesser.network.Player;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import com.hamusuke.numguesser.network.protocol.packet.common.clientbound.ChatNotify;
import com.hamusuke.numguesser.network.protocol.packet.play.clientbound.*;
import com.hamusuke.numguesser.server.game.NormalGame;
import com.hamusuke.numguesser.server.game.seating.SeatingArranger;
import com.hamusuke.numguesser.server.network.ServerPlayer;
import com.hamusuke.numguesser.util.Util;

import javax.annotation.Nullable;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class GameRound {
    protected final NormalGame game;
    protected final List<ServerPlayer> players;
    protected final Random random;
    protected final CardRegistry cardRegistry;
    protected final ParentDeterminer parentDeterminer;
    protected final SeatingArranger seatingArranger;
    protected ServerPlayer curAttacker;
    protected Card curCardForAttacking;
    protected GameState gameState = GameState.STARTING;
    protected CancelOperation cancelOperation = CancelOperation.DO_NOTHING;
    @Nullable
    protected ServerPlayer winner;

    public GameRound(NormalGame game, List<ServerPlayer> players) {
        this.game = game;
        this.players = players;
        this.random = newRandom();
        this.cardRegistry = new CardRegistry(this.random);
        this.parentDeterminer = new ParentDeterminer();
        this.seatingArranger = game.getSeatingArranger();
    }

    protected GameRound(final GameRound old) {
        this.game = old.game;
        this.players = old.players;
        this.random = newRandom();
        this.cardRegistry = new CardRegistry(this.random);
        this.parentDeterminer = old.parentDeterminer;
        this.parentDeterminer.next();
        this.seatingArranger = old.seatingArranger;
    }

    protected static Random newRandom() {
        Random random;
        try {
            random = SecureRandom.getInstanceStrong();
        } catch (NoSuchAlgorithmException e) {
            random = new Random(Util.getMeasuringTimeNano());
        }

        return random;
    }

    public void startRound() {
        this.parentDeterminer.determine(this.players, this.cardRegistry);
        final var parent = this.parentDeterminer.getCurrentParent();
        this.winner = parent;
        this.curAttacker = parent;
        this.sendPacketToAllInGame(new ChatNotify("親は " + parent.getName() + " に決まりました"));
        this.sendPacketToAllInGame(new ChatNotify("親がカードを配ります"));

        this.sendPacketToAllInGame(new SeatingArrangementNotify(this.seatingArranger.getSeatingArrangement()));
        this.giveOutCards();
        this.startAttacking();
    }

    protected SeatingArranger newSeatingArranger() {
        return new SeatingArranger();
    }

    protected void giveOutCards() {
        this.cardRegistry.shuffle(this.parentDeterminer.getCurrentParent().getRandom());
        this.players.forEach(ServerPlayer::makeNewDeck);

        for (var player : this.players) {
            for (int i = 0; i < this.getGivenCardNumPerPlayer(); i++) {
                if (this.cardRegistry.isEmpty()) {
                    break;
                }

                player.getDeck().addCard(this.cardRegistry.pullBy(player));
            }

            player.sendPacket(new PlayerDeckSyncNotify(player.getId(), player.getDeck().getCards().stream().map(Card::toSerializer).toList()));
            this.sendPacketToOthersInGame(player, new PlayerDeckSyncNotify(player.getId(), player.getDeck().getCards().stream().map(Card::toSerializerForOthers).toList()));
        }
    }

    protected void startAttacking() {
        if (this.cardRegistry.isEmpty()) {
            this.selectCardForAttack();
            return;
        }

        final var card = this.cardRegistry.pull();
        this.decideCardForAttacking(card, CancelOperation.DO_NOTHING);
    }

    protected void selectCardForAttack() {
        this.selectCardForAttack(CancelOperation.DO_NOTHING);
    }

    protected void selectCardForAttack(CancelOperation cancelOperation) {
        this.gameState = GameState.SELECTING_CARD_FOR_ATTACKING;
        this.cancelOperation = cancelOperation;
        this.curAttacker.sendPacket(new CardForAttackSelectReq(cancelOperation.isCancellable()));
        this.sendPacketToOthersInGame(this.curAttacker, new RemotePlayerSelectCardForAttackNotify(this.curAttacker));
    }

    public void onCardForAttackSelect(ServerPlayer selector, int id) {
        if (this.curAttacker != selector) { // Not your turn, lol
            return;
        }

        var card = this.cardRegistry.getOwnedCardById(id);
        var cardHolder = this.cardRegistry.getCardOwnerById(id);
        if (cardHolder != this.curAttacker || card == null || card.isOpened()) {
            this.selectCardForAttack(); // Try again
            return;
        }

        this.decideCardForAttacking(card, CancelOperation.BACK_TO_SELECTING_CARD_FOR_ATTACKING);
    }

    protected void ownCard(ServerPlayer player, Card card) {
        if (this.cardRegistry.own(player, card)) {
            int index = player.getDeck().addCard(card);
            player.sendPacket(new PlayerNewCardAddNotify(player.getId(), index, card.toSerializer()));
            this.sendPacketToOthersInGame(player, new PlayerNewCardAddNotify(player.getId(), index, card.toSerializerForOthers()));
        }
    }

    protected void decideCardForAttacking(Card card, CancelOperation cancellable) {
        this.gameState = GameState.ATTACKING;
        this.cancelOperation = cancellable;
        this.curCardForAttacking = card;
        this.curAttacker.sendPacket(new PlayerStartAttackNotify(card.toSerializer(), cancellable.isCancellable()));
        this.sendPacketToOthersInGame(this.curAttacker, new RemotePlayerStartAttackNotify(this.curAttacker.getId(), card.toSerializerForOthers()));
    }

    public void onCancelCommand(ServerPlayer canceller) {
        if (!this.cancelOperation.isCancellable() || this.gameState != GameState.ATTACKING || this.curAttacker != canceller) {
            return;
        }

        switch (this.cancelOperation) {
            case BACK_TO_CONTINUE_OR_STAY -> this.curAttacker.sendPacket(AttackSuccNotify.INSTANCE);
            case BACK_TO_SELECTING_CARD_FOR_ATTACKING -> this.selectCardForAttack();
        }
    }

    public void onCardSelect(ServerPlayer selector, int cardId) {
        if (this.gameState != GameState.ATTACKING || this.curAttacker != selector) {
            return;
        }

        var cardHolder = this.cardRegistry.getCardOwnerById(cardId);
        if (this.curAttacker != cardHolder) { // attacker must select the others' cards.
            this.sendPacketToAllInGame(new PlayerCardSelectionSyncNotify(this.curAttacker.getId(), cardId));
        }
    }

    public void onAttack(ServerPlayer attacker, int id, int num) {
        if (this.curAttacker != attacker) {
            attacker.sendPacket(AttackRsp.INSTANCE);
            return;
        }

        var card = this.cardRegistry.getOwnedCardById(id);
        if (card == null || card.isOpened() || !this.canAttack(card)) {
            return;
        }

        attacker.sendPacket(AttackRsp.INSTANCE);

        this.sendAttackDetailToAll(attacker, num, this.cardRegistry.getCardOwnerById(card.getId()));
        if (card.getNum() == num) {
            this.onAttackSucceeded(card);
        } else {
            this.onAttackFailed(card);
        }
    }

    protected boolean canAttack(Card card) {
        return !this.cardRegistry.isCardOwnedBy(this.curAttacker, card);
    }

    protected void sendAttackDetailToAll(ServerPlayer attacker, int num, ServerPlayer beAttackedPlayer) {
        this.sendPacketToAllInGame(new ChatNotify("アタック: " + attacker.getDisplayName() + "が" + num + "で" + beAttackedPlayer.getDisplayName() + "にアタックしました"));
    }

    protected void onAttackSucceeded(Card card) {
        card.open();
        this.sendPacketToAllInGame(new CardOpenNotify(card.toSerializer()));
        this.sendPacketToAllInGame(new ChatNotify("アタック成功です！"));
        this.giveTipToAttacker(card);

        if (this.shouldEndRound(card)) {
            this.ownCard(this.curAttacker, this.curCardForAttacking);
            this.winner = this.curAttacker;
            this.endRound();
            return;
        }

        this.gameState = GameState.WAITING_PLAYER_CONTINUE_OR_STAY;
        this.curAttacker.sendPacket(AttackSuccNotify.INSTANCE);
    }

    protected void giveTipToAttacker(Card openedCard) {
        var cardHolder = this.cardRegistry.getCardOwnerById(openedCard.getId());
        if (cardHolder != null) {
            cardHolder.subTipPoint(openedCard.getPoint());
        }

        this.curAttacker.addTipPoint(openedCard.getPoint());
    }

    public void continueOrStay(ServerPlayer player, boolean continueAttacking) {
        if (this.curAttacker != player) {
            return;
        }

        if (continueAttacking) {
            this.decideCardForAttacking(this.curCardForAttacking, CancelOperation.BACK_TO_CONTINUE_OR_STAY);
            return;
        }

        this.ownCard(this.curAttacker, this.curCardForAttacking);
        this.endAttacking();
    }

    protected void onAttackFailed(Card closedCard) {
        this.ownCard(this.curAttacker, this.curCardForAttacking);
        this.curCardForAttacking.open();
        this.sendPacketToAllInGame(new CardOpenNotify(this.curCardForAttacking.toSerializer()));
        this.sendPacketToAllInGame(new ChatNotify("アタック失敗です"));

        var closedCardHolder = this.cardRegistry.getCardOwnerById(closedCard.getId());
        if (this.shouldEndRound(this.curCardForAttacking) || this.arePlayersDefeatedBy(closedCardHolder)) {
            // when all players excluding closed card owner are defeated, end the round.
            this.winner = closedCardHolder;
            this.endRound();
            return;
        }

        this.endAttacking();
    }

    protected void endAttacking() {
        this.nextAttacker();
        this.startAttacking();
    }

    protected boolean shouldEndRound(Card openedCard) {
        var player = this.cardRegistry.getCardOwnerById(openedCard.getId());
        if (player == null) {
            this.sendPacketToAllInGame(new ChatNotify(new NullPointerException("player is null").toString()));
            this.sendPacketToAllInGame(new ChatNotify("エラーが発生したのでラウンドを終了します"));
            return true;
        }

        if (player.getDeck().getCards().stream().allMatch(Card::isOpened)) {
            player.setIsDefeated(true);
        }

        return this.arePlayersDefeatedBy(this.curAttacker);
    }

    protected boolean arePlayersDefeatedBy(@Nullable ServerPlayer player) {
        return this.players.stream()
                .filter(sp -> !sp.equals(player))
                .allMatch(ServerPlayer::isDefeated);
    }

    protected void endRound() {
        if (this.gameState == GameState.ENDED) {
            return;
        }

        this.gameState = GameState.ENDED;
        this.sendPacketToAllInGame(new ChatNotify("ラウンド終了"));

        this.giveTipToRoundWinner();

        for (var player : this.players) {
            var list = player.getDeck().openAllCards();
            if (list.isEmpty()) {
                continue;
            }

            this.sendPacketToAllInGame(new CardsOpenNotify(list.stream().map(Card::toSerializer).toList()));
        }

        boolean isFinal = this.isLastRound();
        this.sendPacketToAllInGame(new EndGameRoundNotify(isFinal));

        if (isFinal) {
            this.endFinalRound();
        }
    }

    protected void endFinalRound() {
        this.showWonMessage();
        this.game.onFinalRoundEnded();
    }

    protected void showWonMessage() {
        this.players.stream().max(Comparator.comparingInt(Player::getTipPoint)).ifPresent(winner -> {
            this.sendPacketToAllInGame(new ChatNotify(winner.getDisplayName() + " が" + winner.getTipPoint() + "点で勝利しました！"));
        });
    }

    protected void giveTipToRoundWinner() {
        if (this.winner == null) {
            return;
        }

        int point = this.winner.getDeck().getCards().stream()
                .mapToInt(Card::getPoint)
                .sum();

        this.winner.addTipPoint(point);

        // all defeated players give tip to winner.
        for (var player : this.players) {
            if (player == this.winner) {
                continue;
            }

            player.subTipPoint(point);
        }
    }

    public void ready() {
        if (this.gameState != GameState.ENDED) {
            return;
        }

        if (this.isLastRound()) {
            return;
        }

        if (this.players.stream().allMatch(Player::isReady)) {
            this.players.forEach(player -> player.setReady(false));
            this.game.startNextRound();
        }
    }

    protected void nextAttacker() {
        int cur = this.seatingArranger.getSeatIndex(this.curAttacker);

        for (int i = 0; i < this.seatingArranger.size(); i++) {
            int nextIndex = (cur + 1 + i) % this.seatingArranger.size();
            var player = this.getPlayingPlayerById(this.seatingArranger.get(nextIndex));
            if (player == null || player.isDefeated()) {
                continue;
            }

            this.curAttacker = player;
            break;
        }
    }

    @Nullable
    protected ServerPlayer getPlayingPlayerById(int id) {
        return this.players.stream()
                .filter(player -> player.getId() == id)
                .findFirst()
                .orElse(null);
    }

    public void onPlayerLeft(ServerPlayer player) {
        this.sendPacketToAllInGame(new ChatNotify(player.getDisplayName() + "がゲームをやめました"));
        var list = player.getDeck().openAllCards();
        if (!list.isEmpty()) {
            this.sendPacketToAllInGame(new CardsOpenNotify(list.stream().map(Card::toSerializer).toList()));
        }
        this.parentDeterminer.removeParentCandidate(player);

        if (this.players.size() <= 1) {
            this.winner = this.players.isEmpty() ? null : this.players.get(0);
            this.endRound();
            return;
        }

        player.setReady(false);
        player.setIsDefeated(false);
        this.players.forEach(sp -> sp.setReady(false));

        if (this.curAttacker == player) {
            if (this.gameState == GameState.SELECTING_CARD_FOR_ATTACKING) {
                this.endAttacking();
            } else if (this.gameState == GameState.ATTACKING || this.gameState == GameState.WAITING_PLAYER_CONTINUE_OR_STAY) {
                var temp = this.curCardForAttacking;
                this.continueOrStay(this.curAttacker, false);
                this.sendPacketToAllInGame(new CardOpenNotify(temp.toSerializer()));
            }
        }

        if (this.arePlayersDefeatedBy(this.curAttacker)) {
            this.winner = this.curAttacker;
            this.endRound();
        }
    }

    public void sendPacketToAllInGame(Packet<?> packet) {
        this.sendPacketToOthersInGame(null, packet);
    }

    public void sendPacketToOthersInGame(@Nullable ServerPlayer sender, Packet<?> packet) {
        this.players.stream()
                .filter(player -> !player.equals(sender))
                .forEach(serverPlayer -> serverPlayer.sendPacket(packet));
    }

    public boolean isLastRound() {
        return this.parentDeterminer.hasNoCandidates();
    }

    protected int getGivenCardNumPerPlayer() {
        return switch (this.players.size()) {
            case 2 -> 4;
            case 3 -> 3;
            case 4 -> 2;
            default -> 0;
        };
    }

    public int getDefaultTipPointPerPlayer() {
        return switch (this.players.size()) {
            case 2 -> 400;
            case 3 -> 230;
            case 4 -> 200;
            default -> 0;
        };
    }

    public GameRound newRound() {
        return new GameRound(this);
    }

    public enum GameState {
        STARTING,
        SELECTING_TOSS_OR_ATTACKING,
        TOSSING,
        SELECTING_CARD_FOR_ATTACKING,
        ATTACKING,
        WAITING_PLAYER_CONTINUE_OR_STAY,
        ENDED,
    }

    protected enum CancelOperation {
        DO_NOTHING,
        BACK_TO_SELECTING_TOSS_OR_ATTACKING,
        BACK_TO_CONTINUE_OR_STAY,
        BACK_TO_SELECTING_CARD_FOR_ATTACKING;

        private boolean isCancellable() {
            return this != DO_NOTHING;
        }
    }
}
