package com.hamusuke.numguesser.game;

public enum GameMode {
    GENERIC("通常", 2, 4),
    PAIR_PLAY("ペアプレー", 4, 4);

    public final String name;
    public final int minPlayer;
    public final int maxPlayer;

    GameMode(String name, int minPlayer, int maxPlayer) {
        this.name = name;
        this.minPlayer = minPlayer;
        this.maxPlayer = maxPlayer;
    }
}
