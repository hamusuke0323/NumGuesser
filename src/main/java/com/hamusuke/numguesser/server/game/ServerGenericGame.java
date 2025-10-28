package com.hamusuke.numguesser.server.game;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.hamusuke.numguesser.game.Game;
import com.hamusuke.numguesser.game.data.GameData;
import com.hamusuke.numguesser.network.protocol.packet.play.clientbound.GameDataSyncNotify;
import com.hamusuke.numguesser.server.game.event.GameEventBus;
import com.hamusuke.numguesser.server.game.event.events.GameRoundStartEvent;
import com.hamusuke.numguesser.server.game.round.GameRound;
import com.hamusuke.numguesser.server.game.round.phase.GamePhaseDirector;
import com.hamusuke.numguesser.server.network.ServerPlayer;
import com.hamusuke.numguesser.server.room.ServerRoom;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class ServerGenericGame extends Game {
    protected static final int AUTO_FORCE_EXIT_GAME_TICKS = 60 * 20;
    protected final ServerRoom room;
    protected final List<ServerPlayer> players = Collections.synchronizedList(Lists.newArrayList());
    protected final List<ServerPlayer> playerList;
    protected final GameEventBus eventBus = new GameEventBus();
    protected GameRound round;
    protected int waitForForceExitGameTicks;
    protected final Map<Integer, Object> data = Maps.newConcurrentMap();

    public ServerGenericGame(ServerRoom room, List<ServerPlayer> players) {
        this.room = room;
        this.players.addAll(players);
        this.playerList = Collections.unmodifiableList(this.players);
    }

    public void defineData(final Consumer<Map<Integer, Object>> consumer) {
        consumer.accept(this.data);
    }

    public <V> void defineData(final int id, final V value) {
        this.data.put(id, value);
    }

    public void removeData(final int id) {
        this.data.remove(id);
    }

    public <V> V getData(final int id) {
        return (V) this.data.get(id);
    }

    public <V> void setSyncedData(final GameData<V> data, final V value) {
        this.dataSyncer.set(data, value);
        final var e = this.dataSyncer.getEntry(data);
        if (e.isDirty()) {
            this.players.forEach(player -> player.sendPacket(new GameDataSyncNotify(player, this.dataSyncer.toSerialized(data))));
            e.clearDirty();
        }
    }

    @Override
    public void tick() {
        if (this.waitForForceExitGameTicks > 0) {
            this.waitForForceExitGameTicks--;
            if (this.waitForForceExitGameTicks <= 0) {
                this.room.abortGame();
            }
        }
    }

    public void startGame() {
        if (this.round == null) {
            this.round = this.getFirstRound();
            int point = this.round.getDefaultTipPointPerPlayer();
            this.players.forEach(p -> p.setTipPoint(point));
        }

        this.eventBus.post(new GameRoundStartEvent());
        this.round.startRound();
    }

    public void startNextRound() {
        if (this.getPlayingPlayers().size() == 1) {
            this.room.abortGame();
            return;
        }

        this.round = this.round.newRound();
        this.startGame();
    }

    public void onFinalRoundEnded() {
        this.waitForForceExitGameTicks = AUTO_FORCE_EXIT_GAME_TICKS;
    }

    public synchronized void leavePlayer(ServerPlayer player) {
        if (this.players.remove(player)) {
            this.round.onPlayerLeft(player);
        }
    }

    public void onPlayerAction(final ServerPlayer actor, final Object data) {
        this.round.onPlayerAction(actor, data);
    }

    public void onCancelCommand(ServerPlayer canceller) {
        this.round.onCancelCommand(canceller);
    }

    public void ready() {
        this.round.ready();
    }

    public List<ServerPlayer> getPlayingPlayers() {
        return this.playerList;
    }

    public GameEventBus getEventBus() {
        return this.eventBus;
    }

    protected GameRound getFirstRound() {
        return new GameRound(this, this.playerList, GamePhaseDirector.forNormalGame());
    }
}
