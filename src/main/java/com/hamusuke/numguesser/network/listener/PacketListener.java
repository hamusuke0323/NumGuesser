package com.hamusuke.numguesser.network.listener;

import com.hamusuke.numguesser.network.channel.Connection;
import com.hamusuke.numguesser.network.protocol.PacketDirection;
import com.hamusuke.numguesser.network.protocol.Protocol;
import com.hamusuke.numguesser.network.protocol.packet.Packet;

public interface PacketListener {
    PacketDirection direction();

    Protocol protocol();

    void onDisconnect(String msg);

    default void onPacketError(Packet<?> packet, Exception e) {
        throw new RuntimeException("Packet Error: " + packet.getClass().getSimpleName(), e);
    }

    boolean isAcceptingMessages();

    default boolean shouldHandleMessage(Packet<?> packet) {
        return this.isAcceptingMessages();
    }

    Connection getConnection();
}
