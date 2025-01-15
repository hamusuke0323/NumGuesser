package com.hamusuke.numguesser.game;

public enum GameMode {
    SINGLE_PLAYER_EASY(1, 4, false),
    SINGLE_PLAYER_HARD(1, 4, false),
    NORMAL_GAME(2, 4, true),
    PAIR_PLAY(4, 4, true);

    private final int minPlayer;
    private final int maxPlayer;
    private final boolean canUseTip;

    GameMode(int minPlayer, int maxPlayer, boolean canUseTip) {
        this.minPlayer = minPlayer;
        this.maxPlayer = maxPlayer;
        this.canUseTip = canUseTip;
    }

    public int getMinPlayer() {
        return this.minPlayer;
    }

    public int getMaxPlayer() {
        return this.maxPlayer;
    }

    public boolean canUseTip() {
        return this.canUseTip;
    }
}
