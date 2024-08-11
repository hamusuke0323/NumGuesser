package com.hamusuke.numguesser.network.protocol.packet.clientbound.common;

import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.listener.client.main.ClientCommonPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;

public record PingReq(long serverTime) implements Packet<ClientCommonPacketListener> {
    public PingReq(IntelligentByteBuf byteBuf) {
        this(byteBuf.readLong());
    }

    @Override
    public void write(IntelligentByteBuf byteBuf) {
        byteBuf.writeLong(this.serverTime);
    }

    @Override
    public void handle(ClientCommonPacketListener listener) {
        listener.handlePingPacket(this);
    }
}
