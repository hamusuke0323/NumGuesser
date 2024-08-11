package com.hamusuke.numguesser.game.round;

import com.google.common.collect.Lists;
import com.hamusuke.numguesser.game.card.Card;
import com.hamusuke.numguesser.game.card.Card.CardColor;
import com.hamusuke.numguesser.game.card.Card.CardSerializer;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import com.hamusuke.numguesser.network.protocol.packet.clientbound.common.ChatNotify;
import com.hamusuke.numguesser.network.protocol.packet.clientbound.play.PlayerDeckSyncNotify;
import com.hamusuke.numguesser.server.game.ServerCard;
import com.hamusuke.numguesser.server.network.ServerPlayer;
import com.hamusuke.numguesser.util.Util;

import javax.annotation.Nullable;
import java.util.*;

public abstract class GameRound {
    private static final Set<Card> ALL_CARDS = new HashSet<>();
    protected final List<ServerPlayer> players;
    protected ServerPlayer parent;
    protected final List<Card> deck = Lists.newArrayList(ALL_CARDS);
    protected final Map<ServerPlayer, Card> pulledCardMap = new HashMap<>();

    static {
        for (var color : CardColor.values()) {
            for (int i = 0; i < 12; i++) {
                ALL_CARDS.add(new ServerCard(color, i));
            }
        }
    }

    public GameRound(List<ServerPlayer> players, @Nullable ServerPlayer parent) {
        this.players = players;
        this.parent = parent;
    }

    public void decideParent() {
        if (this.parent != null) {
            return;
        }

        this.pulledCardMap.clear();
        for (var player : this.players) {
            this.pulledCardMap.put(player, Util.chooseRandom(this.deck, player.getRandom()));
        }

        this.pulledCardMap.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .ifPresent(e -> this.parent = e.getKey());

        this.pulledCardMap.remove(this.parent);
        this.sendPacketToAllInGame(new ChatNotify("親は " + this.parent.getName() + " に決まりました"));
        this.sendPacketToAllInGame(new ChatNotify("親がカードを配ります"));

        this.giveOutCards();
    }

    protected void giveOutCards() {
        for (var player : this.players) {
            player.makeNewDeck();

            for (int i = 0; i < this.getGivenCardNumPerPlayer(); i++) {
                int index = this.parent.getRandom().nextInt(this.deck.size());
                player.getDeck().addCard(this.deck.remove(index));
            }

            player.getDeck().sort();
            player.sendPacket(new PlayerDeckSyncNotify(player.getId(), player.getDeck().getCards().stream().map(card -> new CardSerializer(card.getCardColor(), card.getNum())).toList()));
            this.sendPacketToOthersInGame(player, new PlayerDeckSyncNotify(player.getId(), player.getDeck().getCards().stream().map(card -> new CardSerializer(card.getCardColor(), -1)).toList()));
        }
    }

    public void onPlayerLeft(ServerPlayer player) {

    }

    protected void sendPacketToAllInGame(Packet<?> packet) {
        this.sendPacketToOthersInGame(null, packet);
    }

    protected void sendPacketToOthersInGame(@Nullable ServerPlayer sender, Packet<?> packet) {
        this.players.stream()
                .filter(player -> !player.equals(sender))
                .forEach(serverPlayer -> serverPlayer.sendPacket(packet));
    }

    protected abstract int getGivenCardNumPerPlayer();

    protected abstract ServerPlayer nextParent();

    public abstract GameRound newRound();
}
