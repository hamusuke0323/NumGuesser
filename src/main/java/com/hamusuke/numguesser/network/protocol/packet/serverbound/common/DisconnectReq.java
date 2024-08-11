package com.hamusuke.numguesser.network.protocol.packet.serverbound.common;

import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.listener.server.main.ServerCommonPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;

public record DisconnectReq() implements Packet<ServerCommonPacketListener> {
    public DisconnectReq(IntelligentByteBuf ignored) {
        this();
    }

    @Override
    public void write(IntelligentByteBuf byteBuf) {
    }

    @Override
    public void handle(ServerCommonPacketListener listener) {
        listener.handleDisconnect(this);
    }
}
