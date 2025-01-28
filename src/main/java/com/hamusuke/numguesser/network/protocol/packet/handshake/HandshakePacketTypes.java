package com.hamusuke.numguesser.network.protocol.packet.handshake;

import com.hamusuke.numguesser.network.listener.server.handshake.ServerHandshakePacketListener;
import com.hamusuke.numguesser.network.protocol.PacketDirection;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import com.hamusuke.numguesser.network.protocol.packet.PacketType;
import com.hamusuke.numguesser.network.protocol.packet.handshake.serverbound.HandshakeReq;

public class HandshakePacketTypes {
    public static final PacketType<HandshakeReq> HANDSHAKE = createServerbound("handshake");

    private static <T extends Packet<ServerHandshakePacketListener>> PacketType<T> createServerbound(String id) {
        return new PacketType<>(PacketDirection.SERVERBOUND, id);
    }
}
