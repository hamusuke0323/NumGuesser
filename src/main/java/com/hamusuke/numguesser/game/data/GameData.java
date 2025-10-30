package com.hamusuke.numguesser.game.data;

public record GameData<V>(int id, GameDataHandler<V> handler) {
}
