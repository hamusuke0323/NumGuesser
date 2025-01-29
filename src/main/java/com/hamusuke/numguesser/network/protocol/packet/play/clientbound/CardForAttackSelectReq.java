package com.hamusuke.numguesser.network.protocol.packet.play.clientbound;

import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.codec.StreamCodec;
import com.hamusuke.numguesser.network.listener.client.main.ClientPlayPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import com.hamusuke.numguesser.network.protocol.packet.PacketType;
import com.hamusuke.numguesser.network.protocol.packet.play.PlayPacketTypes;

public record CardForAttackSelectReq(boolean cancellable) implements Packet<ClientPlayPacketListener> {
    public static final StreamCodec<IntelligentByteBuf, CardForAttackSelectReq> STREAM_CODEC = Packet.codec(CardForAttackSelectReq::write, CardForAttackSelectReq::new);

    private CardForAttackSelectReq(IntelligentByteBuf buf) {
        this(buf.readBoolean());
    }

    private void write(IntelligentByteBuf buf) {
        buf.writeBoolean(this.cancellable);
    }

    @Override
    public PacketType<CardForAttackSelectReq> type() {
        return PlayPacketTypes.CARD_FOR_ATTACK_SELECT_REQ;
    }

    @Override
    public void handle(ClientPlayPacketListener listener) {
        listener.handleCardForAttackSelect(this);
    }
}
