package com.hamusuke.numguesser.network.protocol.packet.clientbound.common;

import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.listener.client.main.ClientCommonPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;

public record RTTChangeNotify(int id, int rtt) implements Packet<ClientCommonPacketListener> {
    public RTTChangeNotify(IntelligentByteBuf byteBuf) {
        this(byteBuf.readVarInt(), byteBuf.readVarInt());
    }

    @Override
    public void write(IntelligentByteBuf byteBuf) {
        byteBuf.writeVarInt(this.id);
        byteBuf.writeVarInt(this.rtt);
    }

    @Override
    public void handle(ClientCommonPacketListener listener) {
        listener.handleRTTPacket(this);
    }
}
