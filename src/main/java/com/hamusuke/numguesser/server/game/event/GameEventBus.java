package com.hamusuke.numguesser.server.game.event;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.hamusuke.numguesser.server.game.event.events.GameEvent;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class GameEventBus {
    private final Map<Class<? extends GameEvent>, List<EventListener<?>>> listeners = Maps.newConcurrentMap();

    public void register(final Object handler) {
        final var methods = Arrays.stream(handler.getClass().getMethods())
                .filter(m -> m.isAnnotationPresent(EventHandler.class))
                .toList();

        for (final var method : methods) {
            final var params = method.getParameters();
            if (params.length != 1) {
                throw new IllegalArgumentException("Invalid number of parameters: " + method.getName() + " in " + handler.getClass().getName());
            }

            if (!GameEvent.class.isAssignableFrom(params[0].getType())) {
                throw new IllegalArgumentException("Invalid parameter type: " + params[0].getType().getName() + " is not a subclass of GameEvent; " + method.getName() + " in " + handler.getClass().getName());
            }

            this.register((Class<? extends GameEvent>) params[0].getType(), gameEvent -> {
                try {
                    method.invoke(handler, gameEvent);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    public <T extends GameEvent> void register(final Class<T> eventClass, final EventListener<?> listener) {
        this.listeners.computeIfAbsent(eventClass, key -> Lists.newCopyOnWriteArrayList())
                .add(listener);
    }

    public <T extends GameEvent> void post(T event) {
        this.listeners.computeIfAbsent(event.getClass(), key -> Lists.newCopyOnWriteArrayList())
                .forEach(listener -> ((EventListener<T>) listener).accept(event));
    }
}
