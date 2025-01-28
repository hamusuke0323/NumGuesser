package com.hamusuke.numguesser.network.protocol.packet.handshake;

import com.hamusuke.numguesser.network.listener.server.handshake.ServerHandshakePacketListener;
import com.hamusuke.numguesser.network.protocol.Protocol;
import com.hamusuke.numguesser.network.protocol.ProtocolInfo;
import com.hamusuke.numguesser.network.protocol.ProtocolInfoBuilder;
import com.hamusuke.numguesser.network.protocol.packet.handshake.serverbound.HandshakeReq;

public class HandshakeProtocols {
    public static final ProtocolInfo<ServerHandshakePacketListener> SERVERBOUND = ProtocolInfoBuilder
            .serverboundProtocol(Protocol.HANDSHAKING, builder -> builder.addPacket(HandshakePacketTypes.HANDSHAKE, HandshakeReq.STREAM_CODEC));
}
