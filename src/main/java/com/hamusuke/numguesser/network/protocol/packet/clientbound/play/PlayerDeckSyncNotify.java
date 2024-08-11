package com.hamusuke.numguesser.network.protocol.packet.clientbound.play;

import com.google.common.collect.ImmutableList;
import com.hamusuke.numguesser.game.card.Card.CardSerializer;
import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.listener.client.main.ClientPlayPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;

import java.util.List;

public record PlayerDeckSyncNotify(int id, List<CardSerializer> cards) implements Packet<ClientPlayPacketListener> {
    public PlayerDeckSyncNotify(IntelligentByteBuf buf) {
        this(buf.readVarInt(), buf.readList(CardSerializer::new, ImmutableList::copyOf));
    }

    @Override
    public void write(IntelligentByteBuf buf) {
        buf.writeVarInt(this.id);
        buf.writeList(this.cards, CardSerializer::writeTo);
    }

    @Override
    public void handle(ClientPlayPacketListener listener) {
        listener.handlePlayerDeckSync(this);
    }
}
