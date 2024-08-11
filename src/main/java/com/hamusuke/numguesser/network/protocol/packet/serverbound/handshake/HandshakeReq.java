package com.hamusuke.numguesser.network.protocol.packet.serverbound.handshake;

import com.hamusuke.numguesser.Constants;
import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.listener.server.handshake.ServerHandshakePacketListener;
import com.hamusuke.numguesser.network.protocol.Protocol;
import com.hamusuke.numguesser.network.protocol.packet.Packet;

public record HandshakeReq(int protocolVersion,
                           Protocol intendedProtocol) implements Packet<ServerHandshakePacketListener> {
    public HandshakeReq(Protocol intendedProtocol) {
        this(Constants.PROTOCOL_VERSION, intendedProtocol);
    }

    public HandshakeReq(IntelligentByteBuf buf) {
        this(buf.readVarInt(), Protocol.byId(buf.readVarInt()));
    }

    @Override
    public void write(IntelligentByteBuf buf) {
        buf.writeVarInt(this.protocolVersion);
        buf.writeVarInt(this.intendedProtocol.getStateId());
    }

    @Override
    public void handle(ServerHandshakePacketListener listener) {
        listener.handleHandshake(this);
    }

    @Override
    public Protocol nextProtocol() {
        return this.intendedProtocol;
    }
}
