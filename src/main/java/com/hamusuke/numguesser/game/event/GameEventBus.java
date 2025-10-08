package com.hamusuke.numguesser.game.event;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

public abstract class GameEventBus {
    private final Map<Class<? extends GameEvent>, List<EventListener>> listeners = Maps.newConcurrentMap();

    public <T extends GameEvent> void register(final Class<T> eventClass, final EventListener listener) {
        this.listeners.computeIfAbsent(eventClass, key -> Lists.newCopyOnWriteArrayList())
                .add(listener);
    }

    public <T extends GameEvent> void unregister(final Class<T> eventClass, final EventListener listener) {
        this.listeners.getOrDefault(eventClass, Lists.newArrayList()).remove(listener);
    }

    public <T extends GameEvent> void post(T event) {
        this.listeners.computeIfAbsent(event.getClass(), key -> Lists.newCopyOnWriteArrayList())
                .forEach(listener -> listener.accept(event));
    }
}
