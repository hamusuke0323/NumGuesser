package com.hamusuke.numguesser.network.protocol.packet.play.clientbound;

import com.google.common.collect.ImmutableList;
import com.hamusuke.numguesser.game.card.Card.CardSerializer;
import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.codec.StreamCodec;
import com.hamusuke.numguesser.network.listener.client.main.ClientPlayPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import com.hamusuke.numguesser.network.protocol.packet.PacketType;
import com.hamusuke.numguesser.network.protocol.packet.play.PlayPacketTypes;

import java.util.List;

public record CardsOpenNotify(List<CardSerializer> cards) implements Packet<ClientPlayPacketListener> {
    public static final StreamCodec<IntelligentByteBuf, CardsOpenNotify> STREAM_CODEC = Packet.codec(CardsOpenNotify::write, CardsOpenNotify::new);

    private CardsOpenNotify(IntelligentByteBuf buf) {
        this((List<CardSerializer>) buf.readList(CardSerializer::new, ImmutableList::copyOf));
    }

    private void write(IntelligentByteBuf buf) {
        buf.writeList(this.cards, CardSerializer::writeTo);
    }

    @Override
    public PacketType<CardsOpenNotify> type() {
        return PlayPacketTypes.CARDS_OPEN;
    }

    @Override
    public void handle(ClientPlayPacketListener listener) {
        listener.handleCardsOpen(this);
    }
}
