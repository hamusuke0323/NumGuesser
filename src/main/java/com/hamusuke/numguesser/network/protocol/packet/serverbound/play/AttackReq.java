package com.hamusuke.numguesser.network.protocol.packet.serverbound.play;

import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.listener.server.main.ServerPlayPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;

public record AttackReq(int id, int num) implements Packet<ServerPlayPacketListener> {
    public AttackReq(IntelligentByteBuf buf) {
        this(buf.readVarInt(), buf.readVarInt());
    }

    @Override
    public void write(IntelligentByteBuf buf) {
        buf.writeVarInt(this.id);
        buf.writeVarInt(this.num);
    }

    @Override
    public void handle(ServerPlayPacketListener listener) {
        listener.handleAttack(this);
    }
}
