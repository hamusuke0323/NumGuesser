package com.hamusuke.numguesser.client.network.listener.main;

import com.hamusuke.numguesser.client.NumGuesser;
import com.hamusuke.numguesser.client.game.ClientGame;
import com.hamusuke.numguesser.client.game.card.AbstractClientCard;
import com.hamusuke.numguesser.client.game.round.phase.ClientGamePhaseRegistry;
import com.hamusuke.numguesser.client.gui.component.list.CardList.Direction;
import com.hamusuke.numguesser.client.gui.component.panel.main.play.GamePanel;
import com.hamusuke.numguesser.client.gui.component.panel.main.room.RoomPanel;
import com.hamusuke.numguesser.client.room.ClientRoom;
import com.hamusuke.numguesser.network.channel.Connection;
import com.hamusuke.numguesser.network.listener.client.main.ClientPlayPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.play.clientbound.*;
import com.hamusuke.numguesser.network.protocol.packet.play.serverbound.GameExitedNotify;
import com.hamusuke.numguesser.network.protocol.packet.room.RoomProtocols;

import javax.swing.*;

public class ClientPlayPacketListenerImpl extends ClientCommonPacketListenerImpl implements ClientPlayPacketListener {
    public final ClientGame game = new ClientGame(this.curRoom);

    public ClientPlayPacketListenerImpl(NumGuesser client, ClientRoom room, Connection connection) {
        super(client, room, connection);
        this.clientPlayer = client.clientPlayer;
    }

    @Override
    public void tick() {
        super.tick();
        this.game.tick();
    }

    @Override
    public void handleStartGameRound(StartGameRoundNotify packet) {
        this.client.setPanel(new GamePanel());
    }

    @Override
    public void handlePairColorChange(PairColorChangeNotify packet) {
        var player = this.curRoom.getPlayer(packet.id());
        if (player != null) {
            player.setPairColor(packet.color());
            SwingUtilities.invokeLater(this.client.getPanel()::repaint);
        }
    }

    @Override
    public void handlePlayerNewDeck(PlayerNewDeckNotify packet) {
        this.game.clearAllCardMaps();
    }

    @Override
    public void handlePlayerDeckSync(PlayerDeckSyncNotify packet) {
        var player = this.curRoom.getPlayer(packet.id());
        if (player == null) {
            return;
        }

        var deck = this.game.newDeck(player);
        packet.cards().stream()
                .map(AbstractClientCard::from)
                .forEach(card -> {
                    this.game.addCard(player, card);
                    deck.addCard(card);
                });

        final var seatingArrangement = this.game.getSeatingArrangement();
        int myIdIndex = seatingArrangement.indexOf(this.clientPlayer.getId());
        int targetIdIndex = seatingArrangement.indexOf(player.getId());

        if (this.client.getPanel() instanceof GamePanel gamePanel) {
            // when two-player game, only north and south side are used.
            gamePanel.addCardList(seatingArrangement.size() == 2 && player != this.clientPlayer ?
                            Direction.NORTH :
                            Direction.counterClockwiseFromSouth(targetIdIndex - myIdIndex),
                    deck.getOwner().getName(), deck.getCardModel());
        }
    }

    @Override
    public void handleExitGameSucc(ExitGameSuccNotify packet) {
        this.client.getMainWindow().reset(false);
        var listener = new ClientRoomPacketListenerImpl(this.client, this.connection);
        this.connection.setupInboundProtocol(RoomProtocols.CLIENTBOUND, listener);
        this.connection.sendPacket(GameExitedNotify.INSTANCE);
        this.connection.setupOutboundProtocol(RoomProtocols.SERVERBOUND);
        this.client.playerTable.removePointColumn();
        this.client.setPanel(new RoomPanel());
    }

    @Override
    public void handleGameDataSync(GameDataSyncNotify packet) {
        this.game.onGameDataSync(packet.data());
    }

    @Override
    public void handleGamePhaseTransition(GamePhaseTransitionNotify packet) {
        if (!(this.client.getPanel() instanceof GamePanel gamePanel)) {
            return;
        }

        this.game.transitionPhase(ClientGamePhaseRegistry.newPhaseOf(packet.phaseType()), gamePanel);
    }

    @Override
    public void handleTossNotify(TossNotify packet) {
        var openedCard = AbstractClientCard.from(packet.card());
        var card = this.game.getCardById(openedCard.getId());
        if (card == null) {
            return;
        }

        card.setNum(openedCard.getNum());
        card.tossed();

        this.repaintGamePanel();
    }

    @Override
    public void handleCardOpen(CardOpenNotify packet) {
        var openedCard = AbstractClientCard.from(packet.card());
        var card = this.game.getCardById(openedCard.getId());
        if (card == null) {
            return;
        }

        card.setNum(openedCard.getNum());
        card.open();

        this.repaintGamePanel();
    }

    @Override
    public void handleCardsOpen(CardsOpenNotify packet) {
        packet.cards().stream()
                .map(AbstractClientCard::from)
                .forEach(openedCard -> {
                    var card = this.game.getCardById(openedCard.getId());
                    if (card == null) {
                        return;
                    }

                    card.setNum(openedCard.getNum());
                    card.open();
                });

        this.repaintGamePanel();
    }

    @Override
    public void handlePlayerCardSelectionSync(PlayerCardSelectionSyncNotify packet) {
        var player = this.curRoom.getPlayer(packet.playerId());
        var card = this.game.getCardById(packet.cardId());

        if (player == null || card == null) {
            return;
        }

        this.game.selectCard(card);
        card.select(player);

        this.repaintGamePanel();
    }

    @Override
    public void handlePlayerNewCardAdd(PlayerNewCardAddNotify packet) {
        var player = this.curRoom.getPlayer(packet.id());
        if (player == null) {
            return;
        }

        var deck = this.game.getDeckFor(player);
        if (deck == null) {
            return;
        }

        var card = AbstractClientCard.from(packet.card());
        this.game.addCard(player, card);
        deck.addCard(packet.index(), card);
        card.showNewLabel();

        this.repaintGamePanel();
    }

    protected void repaintGamePanel() {
        SwingUtilities.invokeLater(this.client.getMainWindow().getPanel()::repaint);
    }
}
