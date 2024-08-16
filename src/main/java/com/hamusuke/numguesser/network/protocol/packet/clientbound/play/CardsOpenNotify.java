package com.hamusuke.numguesser.network.protocol.packet.clientbound.play;

import com.google.common.collect.ImmutableList;
import com.hamusuke.numguesser.game.card.Card.CardSerializer;
import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.listener.client.main.ClientPlayPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;

import java.util.List;

public record CardsOpenNotify(List<CardSerializer> cards) implements Packet<ClientPlayPacketListener> {
    public CardsOpenNotify(IntelligentByteBuf buf) {
        this((List<CardSerializer>) buf.readList(CardSerializer::new, ImmutableList::copyOf));
    }

    @Override
    public void write(IntelligentByteBuf buf) {
        buf.writeList(this.cards, CardSerializer::writeTo);
    }

    @Override
    public void handle(ClientPlayPacketListener listener) {
        listener.handleCardsOpen(this);
    }
}
