package com.hamusuke.numguesser.client.config;

import com.google.gson.JsonObject;

public class BooleanValue extends ConfigValue<Boolean> {
    public BooleanValue(String name) {
        super(name);
        this.setValue(false);
    }

    @Override
    public void load(JsonObject obj) {
        this.get(obj).ifPresent(e -> this.value = e.getAsBoolean());
    }

    @Override
    public void save(JsonObject obj) {
        obj.addProperty(this.name, this.value);
    }
}
