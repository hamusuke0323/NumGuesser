package com.hamusuke.numguesser.server.game;

import com.hamusuke.numguesser.server.game.event.events.GameMessageEvent;
import com.hamusuke.numguesser.server.game.round.GameRound;
import com.hamusuke.numguesser.server.game.round.PairGameRound;
import com.hamusuke.numguesser.server.network.ServerPlayer;
import com.hamusuke.numguesser.server.room.ServerRoom;

import java.util.List;

public class ServerPairPlayGame extends ServerGame {
    public ServerPairPlayGame(ServerRoom room, List<ServerPlayer> players) {
        super(room, players);
    }

    @Override
    protected GameRound getFirstRound() {
        return new PairGameRound(this, this.players);
    }

    @Override
    public synchronized void leavePlayer(ServerPlayer player) {
        this.players.remove(player);
        this.eventBus.post(new GameMessageEvent("このゲームモードは少なくとも" + this.room.getGameMode().minPlayer + "人必要です"));
        this.room.abortGame();
    }
}
