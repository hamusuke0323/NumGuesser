package com.hamusuke.numguesser.network.protocol.packet.play.serverbound;

import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.codec.StreamCodec;
import com.hamusuke.numguesser.network.listener.server.main.ServerPlayPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import com.hamusuke.numguesser.network.protocol.packet.PacketType;
import com.hamusuke.numguesser.network.protocol.packet.play.PlayPacketTypes;

public record CardForAttackSelectRsp(int id) implements Packet<ServerPlayPacketListener> {
    public static final StreamCodec<IntelligentByteBuf, CardForAttackSelectRsp> STREAM_CODEC = Packet.codec(CardForAttackSelectRsp::write, CardForAttackSelectRsp::new);

    private CardForAttackSelectRsp(IntelligentByteBuf buf) {
        this(buf.readVarInt());
    }

    private void write(IntelligentByteBuf buf) {
        buf.writeVarInt(this.id);
    }

    @Override
    public PacketType<CardForAttackSelectRsp> type() {
        return PlayPacketTypes.CARD_FOR_ATTACK_SELECT_RSP;
    }

    @Override
    public void handle(ServerPlayPacketListener listener) {
        listener.handleCardForAttackSelect(this);
    }
}
