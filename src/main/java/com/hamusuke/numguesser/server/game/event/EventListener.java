package com.hamusuke.numguesser.server.game.event;

import com.hamusuke.numguesser.server.game.event.events.GameEvent;

import java.util.function.Consumer;

public interface EventListener<E extends GameEvent> extends Consumer<E> {
}
