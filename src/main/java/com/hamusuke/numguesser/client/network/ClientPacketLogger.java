package com.hamusuke.numguesser.client.network;

import com.hamusuke.numguesser.client.NumGuesser;
import com.hamusuke.numguesser.network.PacketLogger;

public record ClientPacketLogger(NumGuesser client) implements PacketLogger {
    public ClientPacketLogger(NumGuesser client) {
        this.client = client;
        this.client.packetLogTable.clear();
    }

    @Override
    public void send(PacketDetails details) {
        if (this.client.isPacketTrash(details.packet())) {
            return;
        }

        this.client.packetLogTable.addSent(details);
    }

    @Override
    public void receive(PacketDetails details) {
        if (this.client.isPacketTrash(details.packet())) {
            return;
        }

        this.client.packetLogTable.addReceived(details);
    }
}
