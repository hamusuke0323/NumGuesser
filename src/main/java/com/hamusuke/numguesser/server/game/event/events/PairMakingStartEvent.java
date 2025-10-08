package com.hamusuke.numguesser.server.game.event.events;

import com.hamusuke.numguesser.server.game.pair.ServerPlayerPairRegistry;

public record PairMakingStartEvent(ServerPlayerPairRegistry pairRegistry) implements GameEvent {
}
