package com.hamusuke.numguesser.server.game;

import com.hamusuke.numguesser.game.pair.PlayerPair;
import com.hamusuke.numguesser.server.network.ServerPlayer;

public class ServerPlayerPair extends PlayerPair<ServerPlayer> {
    public ServerPlayerPair(PairColor color) {
        super(color);
    }
}
