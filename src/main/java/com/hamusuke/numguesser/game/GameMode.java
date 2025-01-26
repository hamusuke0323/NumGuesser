package com.hamusuke.numguesser.game;

import com.hamusuke.numguesser.game.mode.NormalGameMode;
import com.hamusuke.numguesser.game.mode.PairPlayGameMode;
import com.hamusuke.numguesser.server.network.ServerPlayer;
import com.hamusuke.numguesser.server.room.ServerRoom;

import java.util.List;

public enum GameMode {
    NORMAL_GAME("ノーマル", 2, 4, NormalGameMode::new),
    PAIR_PLAY("ペアプレー", 4, 4, PairPlayGameMode::new);

    public final String name;
    public final int minPlayer;
    public final int maxPlayer;
    public final GameCreator gameCreator;

    GameMode(String name, int minPlayer, int maxPlayer, GameCreator gameCreator) {
        this.name = name;
        this.minPlayer = minPlayer;
        this.maxPlayer = maxPlayer;
        this.gameCreator = gameCreator;
    }

    public interface GameCreator {
        NormalGameMode createGame(ServerRoom serverRoom, List<ServerPlayer> players);
    }
}
