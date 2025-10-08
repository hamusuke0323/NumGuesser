package com.hamusuke.numguesser.server.game.round;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.hamusuke.numguesser.game.card.Card;
import com.hamusuke.numguesser.game.card.Card.CardColor;
import com.hamusuke.numguesser.network.Player;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import com.hamusuke.numguesser.network.protocol.packet.common.clientbound.ChatNotify;
import com.hamusuke.numguesser.network.protocol.packet.play.clientbound.*;
import com.hamusuke.numguesser.server.game.card.ServerCard;
import com.hamusuke.numguesser.server.game.mode.NormalGameMode;
import com.hamusuke.numguesser.server.network.ServerPlayer;
import com.hamusuke.numguesser.util.Util;

import javax.annotation.Nullable;
import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public class GameRound {
    private final AtomicInteger idIncrementer = new AtomicInteger();
    private final Supplier<Integer> idGenerator = this.idIncrementer::getAndIncrement;
    protected final NormalGameMode game;
    protected final List<ServerPlayer> players;
    protected final List<Integer> seatingArrangement = Lists.newArrayList();
    protected final Random random = new SecureRandom();
    protected ServerPlayer parent;
    protected final List<Card> deck;
    protected final Map<ServerPlayer, Card> pulledCardMapForDecidingParent = Maps.newHashMap();
    protected final Map<Integer, Card> ownCardIdMap = Maps.newConcurrentMap();
    protected final Map<Integer, ServerPlayer> cardIdPlayerMap = Maps.newConcurrentMap();
    protected ServerPlayer curAttacker;
    protected Card curCardForAttacking;
    protected GameState gameState = GameState.STARTING;
    protected CancelOperation cancelOperation = CancelOperation.DO_NOTHING;
    @Nullable
    protected ServerPlayer winner;

    public GameRound(NormalGameMode game, List<ServerPlayer> players, @Nullable ServerPlayer parent) {
        this.game = game;
        this.players = players;
        this.parent = parent;
        this.winner = parent;

        this.deck = Lists.newArrayList();
        for (var color : CardColor.values()) {
            for (int i = 0; i < 12; i++) {
                this.deck.add(new ServerCard(color, i));
            }
        }

        Collections.shuffle(this.deck, this.random);
        this.idIncrementer.set(this.random.nextInt(8));
        this.deck.forEach(card -> card.setId(this.idGenerator.get()));
    }

    public void startRound() {
        this.decideParent();

        this.winner = this.parent;
        this.curAttacker = this.parent;
        this.sendPacketToAllInGame(new ChatNotify("親は " + this.parent.getName() + " に決まりました"));
        this.sendPacketToAllInGame(new ChatNotify("親がカードを配ります"));

        this.setSeatingArrangement();
        this.sendPacketToAllInGame(new SeatingArrangementNotify(this.seatingArrangement));
        this.giveOutCards();
        this.startAttacking();
    }

    protected void setSeatingArrangement() {
        this.seatingArrangement.clear();
        this.seatingArrangement.addAll(this.players.stream().map(Player::getId).toList());
    }

    protected void decideParent() {
        if (this.parent != null) {
            return;
        }

        this.pulledCardMapForDecidingParent.clear();
        for (var player : this.players) {
            this.pulledCardMapForDecidingParent.put(player, Util.chooseRandom(this.deck, player.getRandom()));
        }

        this.nextParent();
    }

    protected void giveOutCards() {
        Collections.shuffle(this.deck, this.parent.getRandom());
        this.players.forEach(ServerPlayer::makeNewDeck);

        for (var player : this.players) {
            for (int i = 0; i < this.getGivenCardNumPerPlayer(); i++) {
                if (this.deck.isEmpty()) {
                    break;
                }

                var card = this.deck.remove(0);
                player.getDeck().addCard(card);
                this.ownCardIdMap.put(card.getId(), card);
                this.cardIdPlayerMap.put(card.getId(), player);
            }

            player.sendPacket(new PlayerDeckSyncNotify(player.getId(), player.getDeck().getCards().stream().map(Card::toSerializer).toList()));
            this.sendPacketToOthersInGame(player, new PlayerDeckSyncNotify(player.getId(), player.getDeck().getCards().stream().map(Card::toSerializerForOthers).toList()));
        }
    }

    protected void startAttacking() {
        var card = this.pullCardFromDeck();
        if (card == null) {
            this.selectCardForAttack();
            return;
        }

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

        var card = this.ownCardIdMap.get(id);
        var cardHolder = this.cardIdPlayerMap.get(id);
        if (cardHolder != this.curAttacker || card == null || card.isOpened()) {
            this.selectCardForAttack(); // Try again
            return;
        }

        this.decideCardForAttacking(card, CancelOperation.BACK_TO_SELECTING_CARD_FOR_ATTACKING);
    }

    @Nullable
    protected Card pullCardFromDeck() {
        if (this.deck.isEmpty()) {
            return null;
        }

        return this.deck.remove(0);
    }

    protected void ownCard(ServerPlayer player, Card card) {
        if (this.ownCardIdMap.put(card.getId(), card) == null) {
            this.cardIdPlayerMap.put(card.getId(), player);
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

        var cardHolder = this.cardIdPlayerMap.get(cardId);
        if (this.curAttacker != cardHolder) { // attacker must select the others' cards.
            this.sendPacketToAllInGame(new PlayerCardSelectionSyncNotify(this.curAttacker.getId(), cardId));
        }
    }

    public void onAttack(ServerPlayer attacker, int id, int num) {
        if (this.curAttacker != attacker) {
            attacker.sendPacket(AttackRsp.INSTANCE);
            return;
        }

        var card = this.ownCardIdMap.get(id);
        if (card == null || card.isOpened() || !this.canAttack(card)) {
            return;
        }

        attacker.sendPacket(AttackRsp.INSTANCE);

        this.sendAttackDetailToAll(attacker, num, this.cardIdPlayerMap.get(card.getId()));
        if (card.getNum() == num) {
            this.onAttackSucceeded(card);
        } else {
            this.onAttackFailed(card);
        }
    }

    protected boolean canAttack(Card card) {
        return this.cardIdPlayerMap.get(card.getId()) != this.curAttacker;
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
        var cardHolder = this.cardIdPlayerMap.get(openedCard.getId());
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

        var closedCardHolder = this.cardIdPlayerMap.get(closedCard.getId());
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
        var player = this.cardIdPlayerMap.get(openedCard.getId());
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

        boolean isFinal = !this.shouldPlayNextRound();
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

        if (!this.shouldPlayNextRound()) {
            return;
        }

        if (this.players.stream().allMatch(Player::isReady)) {
            this.players.forEach(player -> player.setReady(false));
            this.game.startNextRound();
        }
    }

    protected void nextAttacker() {
        int cur = this.seatingArrangement.indexOf(this.curAttacker.getId());

        for (int i = 0; i < this.seatingArrangement.size(); i++) {
            int nextIndex = (cur + 1 + i) % this.seatingArrangement.size();
            var player = this.getPlayingPlayerById(this.seatingArrangement.get(nextIndex));
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
        this.pulledCardMapForDecidingParent.remove(player);

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

    public boolean shouldPlayNextRound() {
        return !this.pulledCardMapForDecidingParent.isEmpty();
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

    protected ServerPlayer nextParent() {
        this.pulledCardMapForDecidingParent.entrySet().stream()
                .min(Map.Entry.comparingByValue())
                .ifPresent(e -> this.parent = e.getKey());
        this.pulledCardMapForDecidingParent.remove(this.parent);

        return this.parent;
    }

    public GameRound newRound() {
        var round = new GameRound(this.game, this.players, this.nextParent());
        round.pulledCardMapForDecidingParent.putAll(this.pulledCardMapForDecidingParent);
        return round;
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
