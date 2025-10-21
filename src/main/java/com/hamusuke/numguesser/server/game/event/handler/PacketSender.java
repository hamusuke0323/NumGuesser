package com.hamusuke.numguesser.server.game.event.handler;

import com.hamusuke.numguesser.event.EventHandler;
import com.hamusuke.numguesser.game.card.Card;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import com.hamusuke.numguesser.network.protocol.packet.common.clientbound.ChatNotify;
import com.hamusuke.numguesser.network.protocol.packet.play.clientbound.*;
import com.hamusuke.numguesser.server.game.event.events.*;
import com.hamusuke.numguesser.server.network.ServerPlayer;

import javax.annotation.Nullable;
import java.util.List;

public final class PacketSender {
    private final List<ServerPlayer> players;

    public PacketSender(final List<ServerPlayer> players) {
        this.players = players;
    }

    @EventHandler
    public void onCardOpen(final CardOpenEvent event) {
        this.sendPacketToAllInGame(new CardOpenNotify(event.card().toSerializer()));
    }

    @EventHandler
    public void onCardsOpen(final CardsOpenEvent event) {
        this.sendPacketToAllInGame(new CardsOpenNotify(event.cards().stream().map(Card::toSerializer).toList()));
    }

    @EventHandler
    public void onGameRoundEnd(final GameRoundEndEvent event) {
        this.sendPacketToAllInGame(new EndGameRoundNotify(event.isFinal()));
    }

    @EventHandler
    public void onGameRoundStart(final GameRoundStartEvent event) {
        this.sendPacketToAllInGame(StartGameRoundNotify.INSTANCE);
    }

    @EventHandler
    public void onGameMessage(final GameMessageEvent event) {
        this.sendPacketToAllInGame(new ChatNotify(event.message()));
    }

    @EventHandler
    public void onPairColorChange(final PairColorChangeEvent event) {
        this.sendPacketToAllInGame(new PairColorChangeNotify(event.player().getId(), event.color()));
    }

    @EventHandler
    public void onPairMakingStart(final PairMakingStartEvent event) {
        this.sendPacketToAllInGame(PairMakingStartNotify.from(event.pairRegistry().toPlayer2ColorMap()));
    }

    @EventHandler
    public void onPlayerCardSelect(final PlayerCardSelectEvent event) {
        this.sendPacketToAllInGame(new PlayerCardSelectionSyncNotify(event.getPlayer().getId(), event.getCardId()));
    }

    @EventHandler
    public void onPlayerDeckSync(final PlayerDeckSyncEvent event) {
        final var player = event.getPlayer();
        player.sendPacket(new PlayerDeckSyncNotify(player.getId(), player.getDeck().getCards().stream().map(Card::toSerializer).toList()));
        this.sendPacketToOthersInGame(player, new PlayerDeckSyncNotify(player.getId(), player.getDeck().getCards().stream().map(Card::toSerializerForOthers).toList()));
    }

    @EventHandler
    public void onPlayerNewCardAdd(final PlayerNewCardAddEvent event) {
        final var player = event.getPlayer();
        player.sendPacket(new PlayerNewCardAddNotify(player.getId(), event.getIndex(), event.getCard().toSerializer()));
        this.sendPacketToOthersInGame(player, new PlayerNewCardAddNotify(player.getId(), event.getIndex(), event.getCard().toSerializerForOthers()));
    }

    @EventHandler
    public void onPlayerSelectCardForAttack(final PlayerSelectCardForAttackEvent event) {
        event.getPlayer().sendPacket(new CardForAttackSelectReq(event.isCancellable()));
        this.sendPacketToOthersInGame(event.getPlayer(), new RemotePlayerSelectCardForAttackNotify(event.getPlayer()));
    }

    @EventHandler
    public void onPlayerSelectCardForToss(final PlayerSelectCardForTossEvent event) {
        event.getPlayer().sendPacket(TossReq.INSTANCE);
        this.sendPacketToOthersInGame(event.getPlayer(), new RemotePlayerSelectCardForTossNotify(event.getPlayer().getId()));
    }

    @EventHandler
    public void onPlayerSelectTossOrAttack(final PlayerSelectTossOrAttackEvent event) {
        event.getPlayer().sendPacket(TossOrAttackSelectionNotify.INSTANCE);
        this.sendPacketToOthersInGame(event.getPlayer(), new RemotePlayerSelectTossOrAttackNotify(event.getPlayer().getId()));
    }

    @EventHandler
    public void onPlayerStartAttack(final PlayerStartAttackEvent event) {
        final var player = event.getPlayer();
        player.sendPacket(new PlayerStartAttackNotify(event.getCard().toSerializer(), event.isCancellable()));
        this.sendPacketToOthersInGame(player, new RemotePlayerStartAttackNotify(player.getId(), event.getCard().toSerializerForOthers()));
    }

    @EventHandler
    public void onSeatingArrangement(final SeatingArrangementEvent event) {
        this.sendPacketToAllInGame(new SeatingArrangementNotify(event.seatingArrangement()));
    }

    private void sendPacketToAllInGame(Packet<?> packet) {
        this.sendPacketToOthersInGame(null, packet);
    }

    private void sendPacketToOthersInGame(@Nullable ServerPlayer sender, Packet<?> packet) {
        this.players.stream()
                .filter(player -> !player.equals(sender))
                .forEach(serverPlayer -> serverPlayer.sendPacket(packet));
    }
}
