package com.hamusuke.numguesser.client.network;

import com.google.common.collect.Lists;
import com.hamusuke.numguesser.client.NumGuesser;
import com.hamusuke.numguesser.client.event.ServerInfoChangeEvent;
import com.hamusuke.numguesser.client.network.listener.info.ClientInfoPacketListenerImpl;
import com.hamusuke.numguesser.network.ServerInfo;
import com.hamusuke.numguesser.network.channel.Connection;

import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ServerChecker {
    private final NumGuesser client = NumGuesser.getInstance();
    private final List<Connection> infoConnections = Collections.synchronizedList(Lists.newArrayList());

    public void tick() {
        this.infoConnections.forEach(Connection::tick);
        this.infoConnections.removeIf(Connection::isDisconnected);
    }

    public void startChecking(final ServerInfo info) {
        CompletableFuture.runAsync(() -> {
            info.status = ServerInfo.Status.CONNECTING;
            this.onServerInfoChanged();
            final var address = new InetSocketAddress(info.address, info.port);
            final var connection = Connection.connectToServer(address, new ClientPacketLogger(this.client));
            connection.initiateServerboundInfoConnection(new ClientInfoPacketListenerImpl(this.client, connection, info));
            this.infoConnections.add(connection);
        }, this.client).exceptionally(throwable -> {
            info.status = ServerInfo.Status.FAILED;
            this.onServerInfoChanged();
            return null;
        });
    }

    private void onServerInfoChanged() {
        this.client.eventBus.post(new ServerInfoChangeEvent());
    }
}
