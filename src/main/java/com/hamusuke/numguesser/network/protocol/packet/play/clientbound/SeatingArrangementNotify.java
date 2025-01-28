package com.hamusuke.numguesser.network.protocol.packet.play.clientbound;

import com.google.common.collect.ImmutableList;
import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.codec.StreamCodec;
import com.hamusuke.numguesser.network.listener.client.main.ClientPlayPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import com.hamusuke.numguesser.network.protocol.packet.PacketType;
import com.hamusuke.numguesser.network.protocol.packet.play.PlayPacketTypes;

import java.util.List;

public record SeatingArrangementNotify(
        List<Integer> serverPlayerIdList) implements Packet<ClientPlayPacketListener> {
    public static final StreamCodec<IntelligentByteBuf, SeatingArrangementNotify> STREAM_CODEC = Packet.codec(SeatingArrangementNotify::write, SeatingArrangementNotify::new);

    private SeatingArrangementNotify(IntelligentByteBuf buf) {
        this((List<Integer>) buf.readList(IntelligentByteBuf::readVarInt, ImmutableList::copyOf));
    }

    private void write(IntelligentByteBuf buf) {
        buf.writeList(this.serverPlayerIdList, (id, buf1) -> buf1.writeVarInt(id));
    }

    @Override
    public PacketType<SeatingArrangementNotify> type() {
        return PlayPacketTypes.SEATING_ARRANGEMENT;
    }

    @Override
    public void handle(ClientPlayPacketListener listener) {
        listener.handleSeatingArrangement(this);
    }
}
