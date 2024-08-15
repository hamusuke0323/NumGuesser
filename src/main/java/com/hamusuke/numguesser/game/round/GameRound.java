package com.hamusuke.numguesser.game.round;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.hamusuke.numguesser.game.card.Card;
import com.hamusuke.numguesser.game.card.Card.CardColor;
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
    protected final List<ServerPlayer> players;
    protected final Random random = new SecureRandom();
    protected ServerPlayer parent;
    protected final List<Card> deck;
    protected final Map<ServerPlayer, Card> pulledCardMap = Maps.newHashMap();
    protected final Map<Integer, Card> playerOwnCardMap = Maps.newConcurrentMap();
    protected ServerPlayer curAttacker;
    protected Card curCardForAttacking;

    public GameRound(List<ServerPlayer> players, @Nullable ServerPlayer parent) {
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
        this.sendPacketToAllInGame(new ChatNotify("親は " + this.parent.getName() + " に決まりました"));
        this.sendPacketToAllInGame(new ChatNotify("親がカードを配ります"));

        this.giveOutCards();
        this.startAttacking();
    }

    protected void decideParent() {
        if (this.parent != null) {
            return;
        }

        this.pulledCardMap.clear();
        for (var player : this.players) {
            this.pulledCardMap.put(player, Util.chooseRandom(this.deck, player.getRandom()));
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
        if (card == null) {
            this.endRound();
            return;
        }

        this.decideCardForAttacking(card);
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
        this.ownCard(this.curAttacker, this.curCardForAttacking);
        this.sendPacketToAllInGame(new ChatNotify("アタック成功です！"));
    }

    protected void onAttackFailed() {
        this.ownCard(this.curAttacker, this.curCardForAttacking);
        this.curCardForAttacking.open();
        this.sendPacketToAllInGame(new CardOpenNotify(this.curCardForAttacking.toSerializer()));
        this.sendPacketToAllInGame(new ChatNotify("アタック失敗です"));
    }

    protected void endAttacking() {
        this.nextAttacker();
        this.startAttacking();
    }

    protected boolean shouldEndRound() {
        return false;
    }

    protected void endRound() {

    }

    protected void nextAttacker() {
        int nextIndex = this.players.indexOf(this.curAttacker) + 1;
        nextIndex = nextIndex >= this.players.size() ? 0 : nextIndex;
        this.curAttacker = this.players.get(nextIndex);
    }

    public void onPlayerLeft(ServerPlayer player) {
        this.sendPacketToAllInGame(new ChatNotify(player.getDisplayName() + "がゲームをやめました"));
        player.getDeck().openAllCards();
        player.getDeck().getCards().forEach(card -> {
            this.sendPacketToAllInGame(new CardOpenNotify(card.toSerializer()));
        });
        this.pulledCardMap.remove(player);
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
        return !this.pulledCardMap.isEmpty();
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
        this.pulledCardMap.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .ifPresent(e -> this.parent = e.getKey());
        this.pulledCardMap.remove(this.parent);

        return this.parent;
    }

    public GameRound newRound() {
        var round = new GameRound(this.players, this.nextParent());
        round.pulledCardMap.putAll(this.pulledCardMap);
        return round;
    }
}
