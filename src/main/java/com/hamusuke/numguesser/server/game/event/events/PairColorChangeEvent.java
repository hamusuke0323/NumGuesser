package com.hamusuke.numguesser.server.game.event.events;

import com.hamusuke.numguesser.game.pair.PlayerPair;
import com.hamusuke.numguesser.server.network.ServerPlayer;

public record PairColorChangeEvent(ServerPlayer player, PlayerPair.PairColor color) implements GameEvent {
}
