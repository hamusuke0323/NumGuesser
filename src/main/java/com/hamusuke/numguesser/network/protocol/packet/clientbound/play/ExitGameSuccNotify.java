package com.hamusuke.numguesser.network.protocol.packet.clientbound.play;

import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.listener.client.main.ClientPlayPacketListener;
import com.hamusuke.numguesser.network.protocol.Protocol;
import com.hamusuke.numguesser.network.protocol.packet.Packet;

public record ExitGameSuccNotify() implements Packet<ClientPlayPacketListener> {
    public ExitGameSuccNotify(IntelligentByteBuf buf) {
        this();
    }

    @Override
    public void write(IntelligentByteBuf buf) {
    }

    @Override
    public void handle(ClientPlayPacketListener listener) {
        listener.handleExitGameSucc(this);
    }

    @Override
    public Protocol nextProtocol() {
        return Protocol.ROOM;
    }
}
