package com.hamusuke.numguesser.server.game.event.events;

import java.util.List;

public record SeatingArrangementEvent(List<Integer> seatingArrangement) implements GameEvent {
}
