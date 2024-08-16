package com.hamusuke.numguesser.client.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Optional;

public abstract class ConfigValue<T> {
    protected final String name;
    protected T value;

    public ConfigValue(String name) {
        this.name = name;
    }

    public Optional<JsonElement> get(JsonObject obj) {
        return Optional.ofNullable(obj.get(this.name));
    }

    public abstract void load(JsonObject obj);

    public abstract void save(JsonObject obj);

    public T getValue() {
        return this.value;
    }

    public void setValue(T value) {
        this.value = value;
    }
}
