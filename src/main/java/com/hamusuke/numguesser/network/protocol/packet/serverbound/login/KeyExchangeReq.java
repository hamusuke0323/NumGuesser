package com.hamusuke.numguesser.network.protocol.packet.serverbound.login;

import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.listener.server.login.ServerLoginPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;

public record KeyExchangeReq() implements Packet<ServerLoginPacketListener> {
    public KeyExchangeReq(IntelligentByteBuf byteBuf) {
        this();
    }

    @Override
    public void write(IntelligentByteBuf byteBuf) {
    }

    @Override
    public void handle(ServerLoginPacketListener listener) {
        listener.handleKeyEx(this);
    }
}
