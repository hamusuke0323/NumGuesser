package com.hamusuke.numguesser.network.protocol.packet.serverbound.login;

import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.listener.server.login.ServerLoginPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;

public record AliveReq() implements Packet<ServerLoginPacketListener> {
    public AliveReq(IntelligentByteBuf byteBuf) {
        this();
    }

    @Override
    public void write(IntelligentByteBuf byteBuf) {
    }

    @Override
    public void handle(ServerLoginPacketListener listener) {
        listener.handlePing(this);
    }
}
