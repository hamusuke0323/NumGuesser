package com.hamusuke.numguesser.server.game.event.events;

public record GameRoundEndEvent(boolean isFinal) implements GameEvent {
}
