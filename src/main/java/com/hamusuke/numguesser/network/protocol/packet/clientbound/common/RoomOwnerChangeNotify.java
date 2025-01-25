package com.hamusuke.numguesser.network.protocol.packet.clientbound.common;

import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.listener.client.main.ClientCommonPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;

public record RoomOwnerChangeNotify(int id) implements Packet<ClientCommonPacketListener> {
    public RoomOwnerChangeNotify(IntelligentByteBuf buf) {
        this(buf.readVarInt());
    }

    @Override
    public void write(IntelligentByteBuf buf) {
        buf.writeVarInt(this.id);
    }

    @Override
    public void handle(ClientCommonPacketListener listener) {
        listener.handleRoomOwnerChange(this);
    }
}
