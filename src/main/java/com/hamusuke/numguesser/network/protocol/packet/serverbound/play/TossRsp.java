package com.hamusuke.numguesser.network.protocol.packet.serverbound.play;

import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.listener.server.main.ServerPlayPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;

public record TossRsp(int cardId) implements Packet<ServerPlayPacketListener> {
    public TossRsp(IntelligentByteBuf buf) {
        this(buf.readVarInt());
    }

    @Override
    public void write(IntelligentByteBuf buf) {
        buf.writeVarInt(this.cardId);
    }

    @Override
    public void handle(ServerPlayPacketListener listener) {
        listener.handleToss(this);
    }
}
