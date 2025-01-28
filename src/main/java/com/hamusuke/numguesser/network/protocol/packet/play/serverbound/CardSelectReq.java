package com.hamusuke.numguesser.network.protocol.packet.play.serverbound;

import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.codec.StreamCodec;
import com.hamusuke.numguesser.network.listener.server.main.ServerPlayPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import com.hamusuke.numguesser.network.protocol.packet.PacketType;
import com.hamusuke.numguesser.network.protocol.packet.play.PlayPacketTypes;

public record CardSelectReq(int id) implements Packet<ServerPlayPacketListener> {
    public static final StreamCodec<IntelligentByteBuf, CardSelectReq> STREAM_CODEC = Packet.codec(CardSelectReq::write, CardSelectReq::new);

    private CardSelectReq(IntelligentByteBuf buf) {
        this(buf.readVarInt());
    }

    private void write(IntelligentByteBuf buf) {
        buf.writeVarInt(this.id);
    }

    @Override
    public PacketType<CardSelectReq> type() {
        return PlayPacketTypes.CARD_SELECT_REQ;
    }

    @Override
    public void handle(ServerPlayPacketListener listener) {
        listener.handleCardSelect(this);
    }
}
