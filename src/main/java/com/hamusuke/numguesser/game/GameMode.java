package com.hamusuke.numguesser.game;

import com.hamusuke.numguesser.game.round.GameRound;
import com.hamusuke.numguesser.game.round.PairGameRound;
import com.hamusuke.numguesser.server.network.ServerPlayer;

import javax.annotation.Nullable;
import java.util.List;

public enum GameMode {
    NORMAL_GAME("ノーマル", 2, 4, GameRound::new),
    PAIR_PLAY("ペアプレー", 4, 4, PairGameRound::new);

    public final String name;
    public final int minPlayer;
    public final int maxPlayer;
    public final GameRoundCreator gameRoundCreator;

    GameMode(String name, int minPlayer, int maxPlayer, GameRoundCreator gameRoundCreator) {
        this.name = name;
        this.minPlayer = minPlayer;
        this.maxPlayer = maxPlayer;
        this.gameRoundCreator = gameRoundCreator;
    }

    public interface GameRoundCreator {
        GameRound createGameRound(NumGuesserGame game, List<ServerPlayer> players, @Nullable ServerPlayer parent);
    }
}
