package com.hamusuke.numguesser.client.game;

import com.google.common.collect.Maps;
import com.hamusuke.numguesser.client.game.card.AbstractClientCard;
import com.hamusuke.numguesser.client.game.card.ClientPlayerDeck;
import com.hamusuke.numguesser.client.game.round.phase.ClientGamePhase;
import com.hamusuke.numguesser.client.gui.component.panel.main.play.GamePanel;
import com.hamusuke.numguesser.client.network.player.AbstractClientPlayer;
import com.hamusuke.numguesser.client.room.ClientRoom;
import com.hamusuke.numguesser.game.Game;
import com.hamusuke.numguesser.game.data.DataListener;
import com.hamusuke.numguesser.game.data.GameData;
import com.hamusuke.numguesser.game.data.GameDataSyncer;

import javax.annotation.Nullable;
import javax.swing.*;
import java.util.List;
import java.util.Map;

public class ClientGame extends Game {
    private final ClientRoom room;
    private final Map<AbstractClientPlayer, ClientPlayerDeck> playerDeckMap = Maps.newConcurrentMap();
    private final Map<Integer, AbstractClientCard> cardMap = Maps.newConcurrentMap();
    private final Map<Integer, AbstractClientPlayer> cardPlayerMap = Maps.newConcurrentMap();
    @Nullable
    private AbstractClientCard curSelectedCard;
    private ClientGamePhase curPhase;

    public ClientGame(final ClientRoom room) {
        this.room = room;
    }

    @Override
    public void tick() {
        this.playerDeckMap.values().forEach(ClientPlayerDeck::tick);
    }

    @Nullable
    public AbstractClientPlayer getPlayer(final int id) {
        return this.room.getPlayer(id);
    }

    public void transitionPhase(final ClientGamePhase phase, final GamePanel panel) {
        final var old = this.curPhase;
        this.curPhase = phase;
        SwingUtilities.invokeLater(() -> {
            if (old != null) {
                old.onExit(this, panel);
            }

            phase.onEnter(this, panel);
            panel.revalidate();
        });
    }

    public <V> void onGameDataSync(final GameDataSyncer.SerializedData<V> data) {
        this.dataSyncer.copyEntryFrom(data);
        if (this.curPhase instanceof DataListener listener) {
            listener.onDataChanged(this.dataSyncer.getEntry(data.entryId()));
        }
    }

    public <V> V getGameData(final GameData<V> data) {
        return this.dataSyncer.get(data);
    }

    public List<Integer> getSeatingArrangement() {
        return this.getGameData(SEATING_ARRANGEMENT);
    }

    public void clearAllCardMaps() {
        this.playerDeckMap.clear();
        this.cardMap.clear();
        this.cardPlayerMap.clear();
    }

    public ClientPlayerDeck newDeck(final AbstractClientPlayer player) {
        final var deck = player.newDeck();
        this.playerDeckMap.put(player, deck);
        return deck;
    }

    public ClientPlayerDeck getDeckFor(final AbstractClientPlayer player) {
        return this.playerDeckMap.get(player);
    }

    public void addCard(final AbstractClientPlayer player, final AbstractClientCard card) {
        this.cardMap.put(card.getId(), card);
        this.cardPlayerMap.put(card.getId(), player);
    }

    public AbstractClientPlayer getCardOwner(final AbstractClientCard card) {
        return this.getCardOwner(card.getId());
    }

    public AbstractClientPlayer getCardOwner(final int id) {
        return this.cardPlayerMap.get(id);
    }

    public AbstractClientCard getCardById(final int id) {
        return this.cardMap.get(id);
    }

    public void clearCardSelection() {
        this.selectCard(null);
    }

    public void selectCard(@Nullable final AbstractClientCard card) {
        if (this.curSelectedCard != null) {
            this.curSelectedCard.select(null);
        }

        this.curSelectedCard = card;
    }
}
