package com.hamusuke.numguesser.server.game.event.events;

import com.hamusuke.numguesser.server.game.round.phase.ServerGamePhase;

public record GamePhaseTransitionEvent(ServerGamePhase phase) implements GameEvent {
}
