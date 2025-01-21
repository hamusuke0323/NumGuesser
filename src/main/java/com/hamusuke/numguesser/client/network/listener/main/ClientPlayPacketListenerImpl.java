package com.hamusuke.numguesser.client.network.listener.main;

import com.google.common.collect.Maps;
import com.hamusuke.numguesser.client.NumGuesser;
import com.hamusuke.numguesser.client.game.card.AbstractClientCard;
import com.hamusuke.numguesser.client.game.card.ClientPlayerDeck;
import com.hamusuke.numguesser.client.gui.component.panel.main.play.GamePanel;
import com.hamusuke.numguesser.client.gui.component.panel.main.room.RoomPanel;
import com.hamusuke.numguesser.client.network.player.AbstractClientPlayer;
import com.hamusuke.numguesser.client.network.player.RemotePlayer;
import com.hamusuke.numguesser.client.room.ClientRoom;
import com.hamusuke.numguesser.game.card.Card.CardSerializer;
import com.hamusuke.numguesser.network.channel.Connection;
import com.hamusuke.numguesser.network.listener.client.main.ClientPlayPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.clientbound.common.PlayerReadySyncNotify;
import com.hamusuke.numguesser.network.protocol.packet.clientbound.common.ReadyRsp;
import com.hamusuke.numguesser.network.protocol.packet.clientbound.play.*;

import javax.annotation.Nullable;
import javax.swing.*;
import java.util.Map;

public class ClientPlayPacketListenerImpl extends ClientCommonPacketListenerImpl implements ClientPlayPacketListener {
    private final Map<AbstractClientPlayer, ClientPlayerDeck> playerDeckMap = Maps.newConcurrentMap();
    private final Map<Integer, AbstractClientCard> cardMap = Maps.newConcurrentMap();
    @Nullable
    private AbstractClientCard curSelectedCard;

    public ClientPlayPacketListenerImpl(NumGuesser client, ClientRoom room, Connection connection) {
        super(client, room, connection);
        this.clientPlayer = client.clientPlayer;
    }

    @Override
    public void tick() {
        super.tick();

        this.playerDeckMap.values().forEach(ClientPlayerDeck::tick);
    }

    @Override
    public void handleStartGameRound(StartGameRoundNotify packet) {
        this.client.setPanel(new GamePanel());
    }

    @Override
    public void handlePlayerNewDeck(PlayerNewDeckNotify packet) {
        this.playerDeckMap.clear();
        this.cardMap.clear();
    }

    @Override
    public void handlePlayerDeckSync(PlayerDeckSyncNotify packet) {
        var player = this.curRoom.getPlayer(packet.id());
        if (player == null) {
            return;
        }

        var deck = this.playerDeckMap.computeIfAbsent(player, AbstractClientPlayer::newDeck);
        packet.cards().stream()
                .map(CardSerializer::toClientCard)
                .forEach(card -> {
                    this.cardMap.put(card.getId(), card);
                    deck.addCard(card);
                });

        if (this.client.getPanel() instanceof GamePanel gamePanel) {
            gamePanel.addCardList(deck.getOwner() == this.clientPlayer, deck.getOwner().getName(), deck.getCardModel());
        }
    }

    @Override
    public void handleExitGameSucc(ExitGameSuccNotify packet) {
        this.client.getMainWindow().reset(false);
        var listener = new ClientRoomPacketListenerImpl(this.client, this.connection);
        this.connection.setListener(listener);
        this.connection.setProtocol(packet.nextProtocol());
        this.client.setPanel(new RoomPanel());
    }

    @Override
    public void handlePlayerStartAttacking(PlayerStartAttackingNotify packet) {
        if (this.client.getPanel() instanceof GamePanel gamePanel) {
            gamePanel.prepareAttacking(packet.card().toClientCard());
        }

        this.repaintGamePanel();
    }

    @Override
    public void handleRemotePlayerStartAttacking(RemotePlayerStartAttackingNotify packet) {
        if (this.curRoom.getPlayer(packet.id()) instanceof RemotePlayer remotePlayer && this.client.getPanel() instanceof GamePanel gamePanel) {
            gamePanel.onRemotePlayerAttacking(remotePlayer);
        }

        this.repaintGamePanel();
    }

    @Override
    public void handleCardOpen(CardOpenNotify packet) {
        var openedCard = packet.card().toClientCard();
        var card = this.cardMap.get(openedCard.getId());
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
                .map(CardSerializer::toClientCard)
                .forEach(openedCard -> {
                    var card = this.cardMap.get(openedCard.getId());
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
        var card = this.cardMap.get(packet.cardId());

        if (player == null || card == null) {
            return;
        }

        if (this.curSelectedCard != null) {
            this.curSelectedCard.select(null);
        }

        this.curSelectedCard = card;
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

        var deck = this.playerDeckMap.get(player);
        if (deck == null) {
            return;
        }

        var card = packet.card().toClientCard();
        this.cardMap.put(card.getId(), card);
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
            gamePanel.onEndRound();
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
