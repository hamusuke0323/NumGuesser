package com.hamusuke.numguesser.event;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class EventBus<E extends Event> {
    private final Class<E> baseEventClass;
    private final Map<Class<? extends E>, List<EventListener<? extends E>>> listeners = Maps.newConcurrentMap();

    public EventBus(final Class<E> baseEventClass) {
        this.baseEventClass = baseEventClass;
    }

    public void register(final Object handler) {
        final var methods = Arrays.stream(handler.getClass().getMethods())
                .filter(m -> m.isAnnotationPresent(EventHandler.class))
                .toList();

        for (final var method : methods) {
            final var params = method.getParameters();
            if (params.length != 1) {
                throw new IllegalArgumentException("Invalid number of parameters: " + method.getName() + " in " + handler.getClass().getName());
            }

            if (!this.baseEventClass.isAssignableFrom(params[0].getType())) {
                throw new IllegalArgumentException("Invalid parameter type: " + params[0].getType().getName() + " is not a subclass of " + this.baseEventClass.getName() + "; " + method.getName() + " in " + handler.getClass().getName());
            }

            this.register((Class<E>) params[0].getType(), event -> {
                try {
                    method.invoke(handler, event);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    public void register(final Class<? extends E> eventClass, final EventListener<? extends E> listener) {
        this.listeners.computeIfAbsent(eventClass, key -> Lists.newCopyOnWriteArrayList())
                .add(listener);
    }

    public <T extends E> void post(final T event) {
        this.listeners.computeIfAbsent((Class<T>) event.getClass(), key -> Lists.newCopyOnWriteArrayList())
                .forEach(listener -> ((EventListener<T>) listener).accept(event));
    }
}
