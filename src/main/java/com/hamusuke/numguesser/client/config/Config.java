package com.hamusuke.numguesser.client.config;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class Config {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new Gson();
    public final BooleanValue darkTheme;
    private final File configFile;
    private final List<ConfigValue<?>> values;

    public Config(String fileName) {
        this.configFile = new File(fileName);
        this.values = Lists.newArrayList();

        this.darkTheme = this.register(new BooleanValue("darkTheme"));
        this.load();
    }

    private <T, V extends ConfigValue<T>> V register(V value) {
        this.values.add(value);
        return value;
    }

    private void load() {
        try {
            if (!this.configFile.exists() || !this.configFile.isFile()) {
                this.configFile.createNewFile();
            }
        } catch (Exception e) {
            LOGGER.warn("Failed to load config", e);
            return;
        }

        if (!this.configFile.exists() || !this.configFile.isFile()) {
            return;
        }

        try (var isr = new InputStreamReader(new FileInputStream(this.configFile), StandardCharsets.UTF_8)) {
            var obj = GSON.fromJson(isr, JsonObject.class);
            if (obj == null) {
                return;
            }

            this.values.forEach(configValue -> configValue.load(obj));
        } catch (Exception e) {
            LOGGER.warn("Failed to load config", e);
        }
    }

    public synchronized void save() {
        try (var jsonWriter = new JsonWriter(new OutputStreamWriter(new FileOutputStream(this.configFile), StandardCharsets.UTF_8))) {
            jsonWriter.setIndent("  ");
            var obj = new JsonObject();
            this.values.forEach(configValue -> configValue.save(obj));
            GSON.toJson(obj, jsonWriter);
        } catch (Exception e) {
            LOGGER.warn("Failed to save config", e);
        }
    }
}
