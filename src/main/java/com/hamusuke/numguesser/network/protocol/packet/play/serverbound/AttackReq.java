package com.hamusuke.numguesser.network.protocol.packet.play.serverbound;

import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.codec.StreamCodec;
import com.hamusuke.numguesser.network.listener.server.main.ServerPlayPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import com.hamusuke.numguesser.network.protocol.packet.PacketType;
import com.hamusuke.numguesser.network.protocol.packet.play.PlayPacketTypes;

public record AttackReq(int id, int num) implements Packet<ServerPlayPacketListener> {
    public static final StreamCodec<IntelligentByteBuf, AttackReq> STREAM_CODEC = Packet.codec(AttackReq::write, AttackReq::new);

    private AttackReq(IntelligentByteBuf buf) {
        this(buf.readVarInt(), buf.readVarInt());
    }

    private void write(IntelligentByteBuf buf) {
        buf.writeVarInt(this.id);
        buf.writeVarInt(this.num);
    }

    @Override
    public PacketType<AttackReq> type() {
        return PlayPacketTypes.ATTACK_REQ;
    }

    @Override
    public void handle(ServerPlayPacketListener listener) {
        listener.handleAttack(this);
    }
}
