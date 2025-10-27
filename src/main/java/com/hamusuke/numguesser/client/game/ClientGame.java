package com.hamusuke.numguesser.client.game;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.hamusuke.numguesser.client.game.card.AbstractClientCard;
import com.hamusuke.numguesser.client.game.card.ClientPlayerDeck;
import com.hamusuke.numguesser.client.game.round.phase.ClientGamePhase;
import com.hamusuke.numguesser.client.network.player.AbstractClientPlayer;
import com.hamusuke.numguesser.game.Game;
import com.hamusuke.numguesser.game.data.DataListener;
import com.hamusuke.numguesser.game.data.GameData;
import com.hamusuke.numguesser.game.data.GameDataSyncer;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public class ClientGame extends Game {
    private final Map<AbstractClientPlayer, ClientPlayerDeck> playerDeckMap = Maps.newConcurrentMap();
    private final Map<Integer, AbstractClientCard> cardMap = Maps.newConcurrentMap();
    private final Map<Integer, AbstractClientPlayer> cardPlayerMap = Maps.newConcurrentMap();
    private final List<Integer> seatingArrangement = Lists.newArrayList();
    @Nullable
    private AbstractClientCard curSelectedCard;
    private ClientGamePhase curPhase;

    @Override
    public void tick() {
        this.playerDeckMap.values().forEach(ClientPlayerDeck::tick);
    }

    public void setPhase(final ClientGamePhase phase) {
        this.curPhase = phase;
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

    public void newSeatingArrangement(final List<Integer> serverPlayerIdList) {
        this.seatingArrangement.clear();
        this.seatingArrangement.addAll(serverPlayerIdList);
    }

    public List<Integer> getSeatingArrangement() {
        return ImmutableList.copyOf(this.seatingArrangement);
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
