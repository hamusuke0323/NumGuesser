package com.hamusuke.numguesser.network.protocol.packet.serverbound.common;

import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.listener.server.main.ServerCommonPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;

public record PongRsp(long serverTime) implements Packet<ServerCommonPacketListener> {
    public PongRsp(IntelligentByteBuf byteBuf) {
        this(byteBuf.readLong());
    }

    @Override
    public void write(IntelligentByteBuf byteBuf) {
        byteBuf.writeLong(this.serverTime);
    }

    @Override
    public void handle(ServerCommonPacketListener listener) {
        listener.handlePongPacket(this);
    }
}
