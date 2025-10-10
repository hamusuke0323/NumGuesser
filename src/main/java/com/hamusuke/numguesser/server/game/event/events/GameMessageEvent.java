package com.hamusuke.numguesser.server.game.event.events;

public record GameMessageEvent(String message) implements GameEvent {
}
