package com.hamusuke.numguesser.network.protocol.packet.clientbound.common;

import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.listener.client.main.ClientCommonPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;

public record PlayerReadySyncNotify(int id, boolean ready) implements Packet<ClientCommonPacketListener> {
    public PlayerReadySyncNotify(IntelligentByteBuf buf) {
        this(buf.readVarInt(), buf.readBoolean());
    }

    @Override
    public void write(IntelligentByteBuf buf) {
        buf.writeVarInt(this.id);
        buf.writeBoolean(this.ready);
    }

    @Override
    public void handle(ClientCommonPacketListener listener) {
        listener.handlePlayerReadySync(this);
    }
}
