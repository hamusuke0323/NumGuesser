package com.hamusuke.numguesser.client.network;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.hamusuke.numguesser.network.ServerInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class ServerInfoRegistry {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new Gson();
    private final File serversFile;
    private final List<ServerInfo> servers = Collections.synchronizedList(Lists.newArrayList());

    public ServerInfoRegistry(final File serversFile) {
        this.serversFile = serversFile;
        this.load();
    }

    private void load() {
        this.servers.clear();
        if (!this.serversFile.exists() || !this.serversFile.isFile()) {
            return;
        }

        try (final var isr = new InputStreamReader(new FileInputStream(this.serversFile), StandardCharsets.UTF_8)) {
            final var servers = GSON.fromJson(isr, JsonArray.class);
            if (servers == null) {
                return;
            }

            Set<ServerInfo> set = Sets.newHashSet();
            servers.forEach(e -> {
                try {
                    final var info = GSON.fromJson(e, ServerInfo.class);
                    if (info == null) {
                        return;
                    }

                    info.status = ServerInfo.Status.NONE;
                    set.add(info);
                } catch (Exception ex) {
                    LOGGER.warn("Error occurred while loading json and skip", ex);
                }
            });

            this.servers.addAll(set);
        } catch (Exception e) {
            LOGGER.warn("Failed to load servers json file", e);
        }
    }

    public synchronized void save() {
        try (final var w = new OutputStreamWriter(new FileOutputStream(this.serversFile), StandardCharsets.UTF_8)) {
            GSON.toJson(this.servers, w);
            w.flush();
        } catch (Exception e) {
            LOGGER.warn("Error occurred while saving servers", e);
        }
    }

    public boolean add(final ServerInfo info) {
        if (this.servers.contains(info)) {
            return false;
        }

        this.servers.add(info);
        return true;
    }

    public void remove(final ServerInfo info) {
        this.servers.remove(info);
    }

    public boolean contains(final ServerInfo info) {
        return this.servers.contains(info);
    }

    public List<ServerInfo> getServers() {
        return ImmutableList.copyOf(this.servers);
    }
}
