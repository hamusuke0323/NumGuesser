package com.hamusuke.numguesser.server;

import com.google.common.collect.Lists;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import com.hamusuke.numguesser.server.network.ServerPlayer;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class PlayerManager {
    private final List<ServerPlayer> players = Lists.newArrayList();
    private final List<ServerPlayer> playerList;

    public PlayerManager() {
        this.playerList = Collections.unmodifiableList(this.players);
    }

    public boolean canJoin(ServerPlayer serverPlayer) {
        return serverPlayer.isAuthorized();
    }

    public void addPlayer(ServerPlayer serverPlayer) {
        synchronized (this.players) {
            this.players.add(serverPlayer);
        }
    }

    public void sendPacketToAll(Packet<?> packet) {
        this.sendPacketToAll(packet, null);
    }

    public void sendPacketToAll(Packet<?> packet, @Nullable GenericFutureListener<? extends Future<? super Void>> callback) {
        synchronized (this.players) {
            this.players.forEach(serverPlayer -> serverPlayer.sendPacket(packet, callback));
        }
    }

    public void removePlayer(ServerPlayer serverPlayer) {
        synchronized (this.players) {
            this.players.remove(serverPlayer);
        }
    }

    public List<ServerPlayer> getPlayers() {
        return this.playerList;
    }
}
