package com.hamusuke.numguesser.network.protocol.packet.serverbound.play;

import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.listener.server.main.ServerPlayPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;

public record CardForAttackSelectRsp(int id) implements Packet<ServerPlayPacketListener> {
    public CardForAttackSelectRsp(IntelligentByteBuf buf) {
        this(buf.readVarInt());
    }

    @Override
    public void write(IntelligentByteBuf buf) {
        buf.writeVarInt(this.id);
    }

    @Override
    public void handle(ServerPlayPacketListener listener) {
        listener.handleCardForAttackSelect(this);
    }
}
