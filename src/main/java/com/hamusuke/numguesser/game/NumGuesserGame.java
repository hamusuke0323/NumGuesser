package com.hamusuke.numguesser.game;

import com.google.common.collect.Lists;
import com.hamusuke.numguesser.game.round.GameRound;
import com.hamusuke.numguesser.game.round.TwoPlayerGameRound;
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
        this.round.decideParent();
    }

    public synchronized void leavePlayer(ServerPlayer player) {
        this.players.remove(player);
        this.round.onPlayerLeft(player);
    }

    private GameRound getFirstRound() {
        return new TwoPlayerGameRound(this.playerList, null);
    }
}
