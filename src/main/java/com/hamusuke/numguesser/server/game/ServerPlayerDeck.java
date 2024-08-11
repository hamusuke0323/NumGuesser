package com.hamusuke.numguesser.server.game;

import com.hamusuke.numguesser.game.card.PlayerDeck;
import com.hamusuke.numguesser.server.network.ServerPlayer;

public class ServerPlayerDeck extends PlayerDeck {
    private final ServerPlayer player;

    public ServerPlayerDeck(ServerPlayer player) {
        this.player = player;
    }

    @Override
    public ServerPlayer getOwner() {
        return this.player;
    }
}
