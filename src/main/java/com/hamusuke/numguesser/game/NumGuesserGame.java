package com.hamusuke.numguesser.game;

import com.google.common.collect.Lists;
import com.hamusuke.numguesser.game.round.GameRound;
import com.hamusuke.numguesser.network.protocol.packet.clientbound.play.StartGameRoundNotify;
import com.hamusuke.numguesser.server.network.ServerPlayer;

import java.util.Collections;
import java.util.List;

public class NumGuesserGame {
    private GameRound round;
    private final List<ServerPlayer> players = Collections.synchronizedList(Lists.newArrayList());
    private final List<ServerPlayer> playerList;

    public NumGuesserGame(List<ServerPlayer> players) {
        this.players.addAll(players);
        this.playerList = Collections.unmodifiableList(this.players);
        this.round = this.getFirstRound();
    }

    public void startGame() {
        this.round.sendPacketToAllInGame(new StartGameRoundNotify());
        this.round.startRound();
    }

    public void startNextRound() {
        this.round = this.round.newRound();
        this.startGame();
    }

    public synchronized void leavePlayer(ServerPlayer player) {
        if (this.players.remove(player)) {
            this.round.onPlayerLeft(player);
        }
    }

    public void onCardSelect(ServerPlayer selector, int id) {
        this.round.onCardSelect(selector, id);
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

    public List<ServerPlayer> getPlayingPlayers() {
        return this.playerList;
    }

    private GameRound getFirstRound() {
        return new GameRound(this, this.playerList, null);
    }
}
