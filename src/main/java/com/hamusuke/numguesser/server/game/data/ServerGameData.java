package com.hamusuke.numguesser.server.game.data;

import com.google.common.collect.Maps;

import java.util.Map;

public class ServerGameData {
    private final Map<ServerGameDataRegistry.DataKey<?>, Object> data = Maps.newConcurrentMap();

    public <T> ServerGameData define(final ServerGameDataRegistry.DataKey<T> id, final T value) {
        this.data.put(id, value);
        return this;
    }

    public <T> void set(final ServerGameDataRegistry.DataKey<T> id, final T value) {
        if (!this.data.containsKey(id)) {
            throw new IllegalArgumentException("server game data (id: " + id.id() + ") is not defined");
        }

        this.data.put(id, value);
    }

    public <T> T get(final ServerGameDataRegistry.DataKey<T> id) {
        return (T) this.data.get(id);
    }
}
