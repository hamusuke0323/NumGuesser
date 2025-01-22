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
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public class GameRound {
    private final AtomicInteger idIncrementer = new AtomicInteger();
    private final Supplier<Integer> idGenerator = this.idIncrementer::getAndIncrement;
    protected final NumGuesserGame game;
    protected final List<ServerPlayer> players;
    protected final Random random = new SecureRandom();
    protected ServerPlayer parent;
    protected final List<Card> deck;
    protected final Map<ServerPlayer, Card> pulledCardMapForDecidingParent = Maps.newHashMap();
    protected final Map<Integer, Card> playerOwnCardMap = Maps.newConcurrentMap();
    protected ServerPlayer curAttacker;
    protected int curAttackerIndex;
    protected Card curCardForAttacking;
    protected Status status = Status.STARTING;

    public GameRound(NumGuesserGame game, List<ServerPlayer> players, @Nullable ServerPlayer parent) {
        this.game = game;
        this.players = players;
        this.parent = parent;

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
        this.curAttackerIndex = this.players.indexOf(this.parent);
        this.sendPacketToAllInGame(new ChatNotify("親は " + this.parent.getName() + " に決まりました"));
        this.sendPacketToAllInGame(new ChatNotify("親がカードを配ります"));

        this.giveOutCards();
        this.startAttacking();
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
                this.playerOwnCardMap.put(card.getId(), card);
            }

            player.sendPacket(new PlayerDeckSyncNotify(player.getId(), player.getDeck().getCards().stream().map(Card::toSerializer).toList()));
            this.sendPacketToOthersInGame(player, new PlayerDeckSyncNotify(player.getId(), player.getDeck().getCards().stream().map(Card::toSerializerForOthers).toList()));
        }
    }

    protected void startAttacking() {
        var card = this.pullCardFromDeck();
        if (card == null && this.shouldAbortRoundWhenDeckIsEmpty()) {
            this.abortRoundDueToLackOfCard();
            this.endRound();
            return;
        }

        this.decideCardForAttacking(Objects.requireNonNull(card));
    }

    protected void abortRoundDueToLackOfCard() {
        this.sendPacketToAllInGame(new ChatNotify("山がなくなったのでラウンドを強制終了します"));
    }

    protected boolean shouldAbortRoundWhenDeckIsEmpty() {
        return true;
    }

    @Nullable
    protected Card pullCardFromDeck() {
        if (this.deck.isEmpty()) {
            return null;
        }

        return this.deck.remove(0);
    }

    protected void ownCard(ServerPlayer player, Card card) {
        this.playerOwnCardMap.put(card.getId(), card);
        int index = player.getDeck().addCard(card);
        player.sendPacket(new PlayerNewCardAddNotify(player.getId(), index, card.toSerializer()));
        this.sendPacketToOthersInGame(player, new PlayerNewCardAddNotify(player.getId(), index, card.toSerializerForOthers()));
    }

    public void decideCardForAttacking(Card card) {
        this.status = Status.ATTACKING;
        this.curCardForAttacking = card;
        this.curAttacker.sendPacket(new PlayerStartAttackingNotify(this.curAttacker.getId(), card.toSerializer()));
        this.sendPacketToOthersInGame(this.curAttacker, new RemotePlayerStartAttackingNotify(this.curAttacker.getId()));
    }

    public void onCardSelect(ServerPlayer selector, int id) {
        if (this.curAttacker == selector) {
            this.sendPacketToAllInGame(new PlayerCardSelectionSyncNotify(this.curAttacker.getId(), id));
        }
    }

    public void onAttack(ServerPlayer attacker, int id, int num) {
        if (this.curAttacker != attacker) {
            attacker.sendPacket(new AttackRsp());
            return;
        }

        var card = this.playerOwnCardMap.get(id);
        if (card == null || card.isOpened()) {
            return;
        }

        attacker.sendPacket(new AttackRsp());
        if (card.getNum() == num) {
            this.onAttackSucceeded(card);
        } else {
            this.onAttackFailed();
        }
    }

    protected void onAttackSucceeded(Card card) {
        card.open();
        this.sendPacketToAllInGame(new CardOpenNotify(card.toSerializer()));
        this.sendPacketToAllInGame(new ChatNotify("アタック成功です！"));

        if (this.shouldEndRound(card)) {
            this.ownCard(this.curAttacker, this.curCardForAttacking);
            this.endRound();
            return;
        }

        this.status = Status.WAITING_PLAYER_CONTINUE_OR_STAY;
        this.curAttacker.sendPacket(new AttackSuccNotify());
    }

    public void continueOrStay(ServerPlayer player, boolean continueAttacking) {
        if (this.curAttacker != player) {
            return;
        }

        if (continueAttacking) {
            this.decideCardForAttacking(this.curCardForAttacking);
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
        if (this.deck.isEmpty() && this.shouldAbortRoundWhenDeckIsEmpty()) {
            this.abortRoundDueToLackOfCard();
            return true;
        }

        for (var player : this.players) {
            if (!player.getDeck().contains(openedCard)) {
                continue;
            }

            if (player.getDeck().getCards().stream().allMatch(Card::isOpened)) {
                return true;
            }
        }

        return false;
    }

    protected void endRound() {
        if (this.status == Status.ENDED) {
            return;
        }

        this.status = Status.ENDED;
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
        if (this.status != Status.ENDED) {
            return;
        }

        if (this.players.stream().allMatch(Player::isReady)) {
            this.players.forEach(player -> player.setReady(false));
            this.game.startNextRound();
        }
    }

    protected void nextAttacker() {
        int cur = this.players.indexOf(this.curAttacker);
        if (cur < 0) {
            cur = this.curAttackerIndex - 1;
        }

        int nextIndex = cur + 1;
        nextIndex = nextIndex >= this.players.size() ? 0 : nextIndex;
        this.curAttacker = this.players.get(nextIndex);
        this.curAttackerIndex = nextIndex;
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
        this.players.forEach(sp -> sp.setReady(false));

        if (this.curAttacker == player) {
            if (this.status == Status.ATTACKING) {
                this.nextAttacker();
                this.decideCardForAttacking(this.curCardForAttacking);
            } else if (this.status == Status.WAITING_PLAYER_CONTINUE_OR_STAY) {
                var temp = this.curCardForAttacking;
                this.continueOrStay(this.curAttacker, false);
                this.sendPacketToAllInGame(new CardOpenNotify(temp.toSerializer()));
            }
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

    public enum Status {
        STARTING,
        SELECTING_TOSS_OR_ATTACKING,
        TOSSING,
        SELECTING_CARD_FOR_ATTACKING,
        ATTACKING,
        WAITING_PLAYER_CONTINUE_OR_STAY,
        ENDED,
    }
}
