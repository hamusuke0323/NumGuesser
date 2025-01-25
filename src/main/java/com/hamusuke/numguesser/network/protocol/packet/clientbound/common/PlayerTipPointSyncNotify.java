package com.hamusuke.numguesser.network.protocol.packet.clientbound.common;

import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.listener.client.main.ClientCommonPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;

public record PlayerTipPointSyncNotify(int id, int tipPoint) implements Packet<ClientCommonPacketListener> {
    public PlayerTipPointSyncNotify(IntelligentByteBuf buf) {
        this(buf.readVarInt(), buf.readVarInt());
    }

    @Override
    public void write(IntelligentByteBuf buf) {
        buf.writeVarInt(this.id);
        buf.writeVarInt(this.tipPoint);
    }

    @Override
    public void handle(ClientCommonPacketListener listener) {
        listener.handlePlayerTipPointSync(this);
    }
}
