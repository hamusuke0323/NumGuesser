package com.hamusuke.numguesser.game;

public enum GameMode {
    NORMAL_GAME(2, 4),
    PAIR_PLAY(4, 4);

    public final int minPlayer;
    public final int maxPlayer;

    GameMode(int minPlayer, int maxPlayer) {
        this.minPlayer = minPlayer;
        this.maxPlayer = maxPlayer;
    }
}
