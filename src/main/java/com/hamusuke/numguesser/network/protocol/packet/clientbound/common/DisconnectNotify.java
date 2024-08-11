package com.hamusuke.numguesser.network.protocol.packet.clientbound.common;

import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.listener.client.main.ClientCommonPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;

public record DisconnectNotify(String msg) implements Packet<ClientCommonPacketListener> {
    public DisconnectNotify(IntelligentByteBuf byteBuf) {
        this(byteBuf.readString());
    }

    @Override
    public void write(IntelligentByteBuf byteBuf) {
        byteBuf.writeString(this.msg);
    }

    @Override
    public void handle(ClientCommonPacketListener listener) {
        listener.handleDisconnectPacket(this);
    }
}
