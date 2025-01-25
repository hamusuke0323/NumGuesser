package com.hamusuke.numguesser.game.round;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.hamusuke.numguesser.game.NumGuesserGame;
import com.hamusuke.numguesser.game.card.Card;
import com.hamusuke.numguesser.game.card.Card.CardColor;
import com.hamusuke.numguesser.network.Player;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import com.hamusuke.numguesser.network.protocol.packet.clientbound.common.ChatNotify;
import com.hamusuke.numguesser.network.protocol.packet.clientbound.play.*;
import com.hamusuke.numguesser.server.game.ServerCard;
import com.hamusuke.numguesser.server.network.ServerPlayer;
import com.hamusuke.numguesser.util.Util;

import javax.annotation.Nullable;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public class GameRound {
    private final AtomicInteger idIncrementer = new AtomicInteger();
    private final Supplier<Integer> idGenerator = this.idIncrementer::getAndIncrement;
    protected final NumGuesserGame game;
    protected final List<ServerPlayer> players;
    protected final List<Integer> seatingArrangement;
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
    protected ServerPlayer winner;

    public GameRound(NumGuesserGame game, List<ServerPlayer> players, @Nullable ServerPlayer parent) {
        this.game = game;
        this.players = players;
        this.seatingArrangement = this.getSeatingArrangement();
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

        this.curAttacker = this.parent;
        this.sendPacketToAllInGame(new ChatNotify("親は " + this.parent.getName() + " に決まりました"));
        this.sendPacketToAllInGame(new ChatNotify("親がカードを配ります"));

        this.sendPacketToAllInGame(new SeatingArrangementNotify(this.seatingArrangement));
        this.giveOutCards();
        this.startAttacking();
    }

    protected List<Integer> getSeatingArrangement() {
        return this.players.stream().map(Player::getId).toList();
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
        this.gameState = GameState.SELECTING_CARD_FOR_ATTACKING;
        this.curAttacker.sendPacket(new CardForAttackSelectReq());
        this.sendPacketToOthersInGame(this.curAttacker, new RemotePlayerSelectCardForAttackNotify(this.curAttacker));
    }

    public void onCardForAttackSelect(ServerPlayer selector, int id) {
        if (this.curAttacker != selector) { // Not your turn, lol
            return;
        }

        var card = this.ownCardIdMap.get(id);
        var player = this.cardIdPlayerMap.get(id);
        if (player == this.curAttacker || card == null || card.isOpened()) {
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
        this.curAttacker.sendPacket(new PlayerStartAttackingNotify(card.toSerializer(), cancellable.isCancellable()));
        this.sendPacketToOthersInGame(this.curAttacker, new RemotePlayerStartAttackingNotify(this.curAttacker.getId(), card.toSerializerForOthers()));
    }

    public void onCancelCommand(ServerPlayer canceller) {
        if (!this.cancelOperation.isCancellable() || this.gameState != GameState.ATTACKING || this.curAttacker != canceller) {
            return;
        }

        switch (this.cancelOperation) {
            case BACK_TO_CONTINUE_OR_STAY -> this.curAttacker.sendPacket(new AttackSuccNotify());
            case BACK_TO_SELECTING_CARD_FOR_ATTACKING -> this.selectCardForAttack();
        }
    }

    public void onCardSelect(ServerPlayer selector, int id) {
        if (this.gameState != GameState.ATTACKING) {
            return;
        }

        if (this.curAttacker == selector) {
            this.sendPacketToAllInGame(new PlayerCardSelectionSyncNotify(this.curAttacker.getId(), id));
        }
    }

    public void onAttack(ServerPlayer attacker, int id, int num) {
        if (this.curAttacker != attacker) {
            attacker.sendPacket(new AttackRsp());
            return;
        }

        var card = this.ownCardIdMap.get(id);
        if (card == null || card.isOpened()) {
            return;
        }

        attacker.sendPacket(new AttackRsp());

        this.sendAttackDetailToAll(attacker, num, this.cardIdPlayerMap.get(card.getId()));
        if (card.getNum() == num) {
            this.onAttackSucceeded(card);
        } else {
            this.onAttackFailed();
        }
    }

    protected void sendAttackDetailToAll(ServerPlayer attacker, int num, ServerPlayer beAttackedPlayer) {
        this.sendPacketToAllInGame(new ChatNotify("アタック: " + attacker.getDisplayName() + "が" + num + "で" + beAttackedPlayer.getDisplayName() + "にアタックしました"));
    }

    protected void onAttackSucceeded(Card card) {
        card.open();
        this.sendPacketToAllInGame(new CardOpenNotify(card.toSerializer()));
        this.sendPacketToAllInGame(new ChatNotify("アタック成功です！"));

        if (this.shouldEndRound(card)) {
            this.ownCard(this.curAttacker, this.curCardForAttacking);
            this.winner = this.curAttacker;
            this.endRound();
            return;
        }

        this.gameState = GameState.WAITING_PLAYER_CONTINUE_OR_STAY;
        this.curAttacker.sendPacket(new AttackSuccNotify());
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

    protected void onAttackFailed() {
        this.ownCard(this.curAttacker, this.curCardForAttacking);
        this.curCardForAttacking.open();
        this.sendPacketToAllInGame(new CardOpenNotify(this.curCardForAttacking.toSerializer()));
        this.sendPacketToAllInGame(new ChatNotify("アタック失敗です"));

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

        for (var player : this.players) {
            var list = player.getDeck().openAllCards();
            if (list.isEmpty()) {
                continue;
            }

            this.sendPacketToAllInGame(new CardsOpenNotify(list.stream().map(Card::toSerializer).toList()));
        }

        this.sendPacketToAllInGame(new EndGameRoundNotify());
    }

    public void ready() {
        if (this.gameState != GameState.ENDED) {
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

    protected boolean shouldPlayNextRound() {
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

    protected ServerPlayer nextParent() {
        this.pulledCardMapForDecidingParent.entrySet().stream()
                .max(Map.Entry.comparingByValue())
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
