package com.hamusuke.numguesser.network.protocol.packet.clientbound.play;

import com.google.common.collect.ImmutableList;
import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.listener.client.main.ClientPlayPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;

import java.util.List;

public record SeatingArrangementNotify(
        List<Integer> serverPlayerIdList) implements Packet<ClientPlayPacketListener> {
    public SeatingArrangementNotify(IntelligentByteBuf buf) {
        this((List<Integer>) buf.readList(IntelligentByteBuf::readVarInt, ImmutableList::copyOf));
    }

    @Override
    public void write(IntelligentByteBuf buf) {
        buf.writeList(this.serverPlayerIdList, (id, buf1) -> buf1.writeVarInt(id));
    }

    @Override
    public void handle(ClientPlayPacketListener listener) {
        listener.handleSeatingArrangement(this);
    }
}
