package com.hamusuke.numguesser.network.protocol.packet.serverbound.info;

import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.listener.server.info.ServerInfoPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;

public record ServerInfoReq(long clientTime) implements Packet<ServerInfoPacketListener> {
    public ServerInfoReq(IntelligentByteBuf buf) {
        this(buf.readLong());
    }

    @Override
    public void write(IntelligentByteBuf buf) {
        buf.writeLong(this.clientTime);
    }

    @Override
    public void handle(ServerInfoPacketListener listener) {
        listener.handleInfoReq(this);
    }
}
