package com.hamusuke.numguesser.network.protocol.packet.serverbound.common;

import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.listener.server.main.ServerCommonPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;

public record ReadyReq() implements Packet<ServerCommonPacketListener> {
    public ReadyReq(IntelligentByteBuf buf) {
        this();
    }

    @Override
    public void write(IntelligentByteBuf buf) {
    }

    @Override
    public void handle(ServerCommonPacketListener listener) {
        listener.handleReady(this);
    }
}
