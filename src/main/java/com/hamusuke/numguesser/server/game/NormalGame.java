package com.hamusuke.numguesser.server.game;

import com.google.common.collect.Lists;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import com.hamusuke.numguesser.server.game.event.GameEventBus;
import com.hamusuke.numguesser.server.game.event.events.GameRoundStartEvent;
import com.hamusuke.numguesser.server.game.event.handler.PacketSender;
import com.hamusuke.numguesser.server.game.round.GameRound;
import com.hamusuke.numguesser.server.game.round.phase.GamePhaseDirector;
import com.hamusuke.numguesser.server.game.seating.SeatingArranger;
import com.hamusuke.numguesser.server.network.ServerPlayer;
import com.hamusuke.numguesser.server.room.ServerRoom;

import java.util.Collections;
import java.util.List;

public class NormalGame {
    protected static final int AUTO_FORCE_EXIT_GAME_TICKS = 60 * 20;
    protected final ServerRoom room;
    protected final List<ServerPlayer> players = Collections.synchronizedList(Lists.newArrayList());
    protected final List<ServerPlayer> playerList;
    protected final SeatingArranger seatingArranger;
    protected final GameEventBus eventBus = new GameEventBus();
    protected GameRound round;
    protected boolean isFirstRound = true;
    protected int waitForForceExitGameTicks;

    public NormalGame(ServerRoom room, List<ServerPlayer> players) {
        this.room = room;
        this.players.addAll(players);
        this.playerList = Collections.unmodifiableList(this.players);
        this.seatingArranger = this.newSeatingArranger();
        this.seatingArranger.arrange(this.playerList);
        this.eventBus.register(new PacketSender(this.playerList));
    }

    protected SeatingArranger newSeatingArranger() {
        return new SeatingArranger();
    }

    public void tick() {
        if (this.waitForForceExitGameTicks > 0) {
            this.waitForForceExitGameTicks--;
            if (this.waitForForceExitGameTicks <= 0) {
                this.room.abortGame();
            }
        }
    }

    public void startGame() {
        if (this.isFirstRound) {
            this.round = this.getFirstRound();
            this.isFirstRound = false;
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

    public void onPlayerAction(final ServerPlayer actor, final Packet<?> packet) {
        this.round.onPlayerAction(actor, packet);
    }

    public void onCancelCommand(ServerPlayer canceller) {
        this.round.onCancelCommand(canceller);
    }

    public void ready() {
        this.round.ready();
    }

    public SeatingArranger getSeatingArranger() {
        return this.seatingArranger;
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
