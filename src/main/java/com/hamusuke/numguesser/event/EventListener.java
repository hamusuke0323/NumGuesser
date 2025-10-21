package com.hamusuke.numguesser.event;

import java.util.function.Consumer;

public interface EventListener<E extends Event> extends Consumer<E> {
}
