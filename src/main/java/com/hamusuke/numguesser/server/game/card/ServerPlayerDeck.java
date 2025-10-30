package com.hamusuke.numguesser.server.game.card;

import com.hamusuke.numguesser.game.card.PlayerDeck;
import com.hamusuke.numguesser.server.network.ServerPlayer;

public class ServerPlayerDeck extends PlayerDeck<ServerCard, ServerPlayer> {
    public ServerPlayerDeck(ServerPlayer player) {
        super(player);
    }
}
