package com.hamusuke.numguesser.server.game.event.handler;

import com.hamusuke.numguesser.event.EventHandler;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import com.hamusuke.numguesser.network.protocol.packet.common.clientbound.ChatNotify;
import com.hamusuke.numguesser.network.protocol.packet.play.clientbound.*;
import com.hamusuke.numguesser.server.game.card.ServerCard;
import com.hamusuke.numguesser.server.game.event.events.*;
import com.hamusuke.numguesser.server.network.ServerPlayer;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Function;

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
        this.sendPacketToAllInGame(new CardsOpenNotify(event.cards().stream().map(ServerCard::toSerializer).toList()));
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
    public void onGamePhaseTransition(final GamePhaseTransitionEvent event) {
        this.sendPacketToAllInGame(new GamePhaseTransitionNotify(event.phase().type()));
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
        final var deckOwner = event.getPlayer();
        this.sendPacketToAllInGame(player ->
                new PlayerDeckSyncNotify(deckOwner.getId(),
                        deckOwner.getDeck().getCards().stream()
                                .map(card ->
                                        card.toSerializer(ServerCard.VisibleTester.OnlyOwner.testFor(player))).toList()));
    }

    @EventHandler
    public void onPlayerNewCardAdd(final PlayerNewCardAddEvent event) {
        final var cardOwner = event.getPlayer();
        this.sendPacketToAllInGame(player ->
                new PlayerNewCardAddNotify(cardOwner.getId(), event.getIndex(),
                        event.getCard().toSerializer(ServerCard.VisibleTester.OnlyOwner.testFor(player))));
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
        final var attacker = event.getPlayer();
        attacker.sendPacket(new PlayerStartAttackNotify(event.getCard().toSerializer(), event.isCancellable()));
        this.sendPacketToOthersInGame(attacker, new RemotePlayerStartAttackNotify(attacker.getId(), event.getCard().toSerializer(ServerCard.VisibleTester.NEVER)));
    }

    @EventHandler
    public void onSeatingArrangement(final SeatingArrangementEvent event) {
        this.sendPacketToAllInGame(new SeatingArrangementNotify(event.seatingArrangement()));
    }

    @EventHandler
    public void onToss(final TossEvent event) {
        event.attacker().sendPacket(new TossNotify(event.card().toSerializer()));
        event.attacker().sendPacket(new ChatNotify("味方があなたにトスしました"));
    }

    private void sendPacketToAllInGame(Packet<?> packet) {
        this.sendPacketToOthersInGame(null, packet);
    }

    private void sendPacketToAllInGame(final Function<ServerPlayer, Packet<?>> packetSupplier) {
        this.players.forEach(player -> player.sendPacket(packetSupplier.apply(player)));
    }

    private void sendPacketToOthersInGame(@Nullable ServerPlayer sender, Packet<?> packet) {
        this.players.stream()
                .filter(player -> !player.equals(sender))
                .forEach(serverPlayer -> serverPlayer.sendPacket(packet));
    }
}
