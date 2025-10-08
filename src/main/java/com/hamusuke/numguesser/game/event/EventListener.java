package com.hamusuke.numguesser.game.event;

import java.util.function.Consumer;

public interface EventListener<E extends GameEvent> extends Consumer<E> {
}
