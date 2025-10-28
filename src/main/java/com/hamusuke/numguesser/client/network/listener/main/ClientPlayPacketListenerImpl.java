package com.hamusuke.numguesser.client.network.listener.main;

import com.hamusuke.numguesser.client.NumGuesser;
import com.hamusuke.numguesser.client.game.ClientGame;
import com.hamusuke.numguesser.client.game.card.AbstractClientCard;
import com.hamusuke.numguesser.client.game.round.phase.ClientGamePhaseRegistry;
import com.hamusuke.numguesser.client.gui.component.list.CardList.Direction;
import com.hamusuke.numguesser.client.gui.component.panel.main.play.GamePanel;
import com.hamusuke.numguesser.client.gui.component.panel.main.play.PairMakingPanel;
import com.hamusuke.numguesser.client.gui.component.panel.main.room.RoomPanel;
import com.hamusuke.numguesser.client.network.player.RemotePlayer;
import com.hamusuke.numguesser.client.room.ClientRoom;
import com.hamusuke.numguesser.network.Player;
import com.hamusuke.numguesser.network.channel.Connection;
import com.hamusuke.numguesser.network.listener.client.main.ClientPlayPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.common.clientbound.PlayerReadySyncNotify;
import com.hamusuke.numguesser.network.protocol.packet.common.clientbound.ReadyRsp;
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
    public void handlePairMakingStart(PairMakingStartNotify packet) {
        var map = packet.toPlayerPairMap(this.curRoom::getPlayer);
        map.forEach(Player::setPairColor);
        this.client.setPanel(new PairMakingPanel(map.keySet().stream().toList()));
    }

    @Override
    public void handlePairColorChange(PairColorChangeNotify packet) {
        var player = this.curRoom.getPlayer(packet.id());
        if (player != null) {
            player.setPairColor(packet.color());

            if (this.client.getPanel() instanceof PairMakingPanel panel) {
                SwingUtilities.invokeLater(panel::repaint);
            }
        }
    }

    @Override
    public void handleStartGameRound(StartGameRoundNotify packet) {
        this.client.setPanel(new GamePanel());
    }

    @Override
    public void handleSeatingArrangement(SeatingArrangementNotify packet) {
        //this.game.newSeatingArrangement(packet.serverPlayerIdList());
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
    public void handleTossOrAttackSelection(TossOrAttackSelectionNotify packet) {
        if (this.client.getPanel() instanceof GamePanel gamePanel) {
            gamePanel.onSelectTossOrAttack();
        }

        this.repaintGamePanel();
    }

    @Override
    public void handleTossReq(TossReq packet) {
        if (this.client.getPanel() instanceof GamePanel gamePanel) {
            gamePanel.onTossReq();
        }

        this.repaintGamePanel();
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
    public void handleCardForAttackSelect(CardForAttackSelectReq packet) {
        this.game.clearCardSelection();
        if (this.client.getPanel() instanceof GamePanel gamePanel) {
            //gamePanel.onSelectCardForAttackReq(packet.cancellable());
        }

        this.repaintGamePanel();
    }

    @Override
    public void handleRemotePlayerSelectTossOrAttack(RemotePlayerSelectTossOrAttackNotify packet) {
        this.game.clearCardSelection();
        if (this.curRoom.getPlayer(packet.id()) instanceof RemotePlayer remotePlayer && this.client.getPanel() instanceof GamePanel gamePanel) {
            gamePanel.onRemotePlayerSelectTossOrAttack(remotePlayer);
        }
    }

    @Override
    public void handleRemotePlayerSelectCardForToss(RemotePlayerSelectCardForTossNotify packet) {
        this.game.clearCardSelection();
        if (this.curRoom.getPlayer(packet.id()) instanceof RemotePlayer remotePlayer && this.client.getPanel() instanceof GamePanel gamePanel) {
            gamePanel.onRemotePlayerSelectCardForToss(remotePlayer);
        }
    }

    @Override
    public void handleRemotePlayerSelectCardForAttack(RemotePlayerSelectCardForAttackNotify packet) {
        this.game.clearCardSelection();
        if (this.curRoom.getPlayer(packet.id()) instanceof RemotePlayer remotePlayer && this.client.getPanel() instanceof GamePanel gamePanel) {
            //gamePanel.onRemotePlayerSelectCardForAttack(remotePlayer);
        }
    }

    @Override
    public void handlePlayerStartAttacking(PlayerStartAttackNotify packet) {
        this.game.clearCardSelection();
        if (this.client.getPanel() instanceof GamePanel gamePanel) {
            gamePanel.prepareAttacking(AbstractClientCard.from(packet.card()), packet.cancellable());
        }

        this.repaintGamePanel();
    }

    @Override
    public void handleRemotePlayerStartAttacking(RemotePlayerStartAttackNotify packet) {
        this.game.clearCardSelection();
        if (this.curRoom.getPlayer(packet.id()) instanceof RemotePlayer remotePlayer && this.client.getPanel() instanceof GamePanel gamePanel) {
            remotePlayer.onAttack(AbstractClientCard.from(packet.cardForAttack()));
            gamePanel.onRemotePlayerAttacking(remotePlayer);
        }

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
    public void handleAttack(AttackRsp packet) {
        if (this.client.getPanel() instanceof GamePanel gamePanel) {
            gamePanel.setAttackBtnEnabled(false);
        }

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

    @Override
    public void handleAttackSucc(AttackSuccNotify packet) {
        if (this.client.getPanel() instanceof GamePanel gamePanel) {
            gamePanel.attackSucceeded();
        }

        this.repaintGamePanel();
    }

    @Override
    public void handleEndGameRound(EndGameRoundNotify packet) {
        if (this.client.getPanel() instanceof GamePanel gamePanel) {
            gamePanel.onEndRound(packet.isFinalRound());
        }

        this.repaintGamePanel();
    }

    @Override
    public void handlePlayerReadySync(PlayerReadySyncNotify packet) {
        super.handlePlayerReadySync(packet);

        if (this.client.getPanel() instanceof GamePanel gamePanel) {
            gamePanel.onReadySync();
        }

        this.repaintGamePanel();
    }

    @Override
    public void handleReadyRsp(ReadyRsp packet) {
        if (this.client.getPanel() instanceof GamePanel gamePanel) {
            gamePanel.onReadyRsp();
        }
    }

    protected void repaintGamePanel() {
        SwingUtilities.invokeLater(this.client.getMainWindow().getPanel()::repaint);
    }
}
