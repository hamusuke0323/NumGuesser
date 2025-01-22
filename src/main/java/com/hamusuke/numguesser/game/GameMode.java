package com.hamusuke.numguesser.game;

public enum GameMode {
    NORMAL_GAME(2, 4),
    PAIR_PLAY(4, 4);

    private final int minPlayer;
    private final int maxPlayer;

    GameMode(int minPlayer, int maxPlayer) {
        this.minPlayer = minPlayer;
        this.maxPlayer = maxPlayer;
    }

    public int getMinPlayer() {
        return this.minPlayer;
    }

    public int getMaxPlayer() {
        return this.maxPlayer;
    }
}
