package com.hamusuke.numguesser.game.mode;

import com.google.common.collect.Lists;
import com.hamusuke.numguesser.game.round.GameRound;
import com.hamusuke.numguesser.network.listener.client.main.ClientCommonPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import com.hamusuke.numguesser.network.protocol.packet.play.clientbound.StartGameRoundNotify;
import com.hamusuke.numguesser.server.network.ServerPlayer;
import com.hamusuke.numguesser.server.room.ServerRoom;

import java.util.Collections;
import java.util.List;

public class NormalGameMode {
    protected static final int AUTO_FORCE_EXIT_GAME_TICKS = 60 * 20;
    protected final ServerRoom room;
    protected final List<ServerPlayer> players = Collections.synchronizedList(Lists.newArrayList());
    protected final List<ServerPlayer> playerList;
    protected GameRound round;
    protected boolean isFirstRound = true;
    protected int waitForForceExitGameTicks;

    public NormalGameMode(ServerRoom room, List<ServerPlayer> players) {
        this.room = room;
        this.players.addAll(players);
        this.playerList = Collections.unmodifiableList(this.players);
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

        this.round.sendPacketToAllInGame(StartGameRoundNotify.INSTANCE);
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

    protected void sendPacketToAllInGame(Packet<? extends ClientCommonPacketListener> packet) {
        this.players.forEach(p -> p.sendPacket(packet));
    }

    public void onCardSelect(ServerPlayer selector, int id) {
        this.round.onCardSelect(selector, id);
    }

    public void onCardForAttackSelect(ServerPlayer selector, int id) {
        this.round.onCardForAttackSelect(selector, id);
    }

    public void onCancelCommand(ServerPlayer canceller) {
        this.round.onCancelCommand(canceller);
    }

    public void onTossSelected(ServerPlayer selector) {
    }

    public void onAttackSelected(ServerPlayer selector) {
    }

    public void onToss(ServerPlayer tosser, int cardId) {
    }

    public void onAttack(ServerPlayer player, int id, int num) {
        this.round.onAttack(player, id, num);
    }

    public void continueAttacking(ServerPlayer player) {
        this.round.continueOrStay(player, true);
    }

    public void stay(ServerPlayer player) {
        this.round.continueOrStay(player, false);
    }

    public void ready() {
        this.round.ready();
    }

    protected GameRound getRound() {
        return this.round;
    }

    public List<ServerPlayer> getPlayingPlayers() {
        return this.playerList;
    }

    protected GameRound getFirstRound() {
        return new GameRound(this, this.playerList, null);
    }
}
