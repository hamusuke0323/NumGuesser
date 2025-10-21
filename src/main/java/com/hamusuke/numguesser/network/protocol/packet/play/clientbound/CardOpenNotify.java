package com.hamusuke.numguesser.network.protocol.packet.play.clientbound;

import com.hamusuke.numguesser.game.card.CardSerializer;
import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.codec.StreamCodec;
import com.hamusuke.numguesser.network.listener.client.main.ClientPlayPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import com.hamusuke.numguesser.network.protocol.packet.PacketType;
import com.hamusuke.numguesser.network.protocol.packet.play.PlayPacketTypes;

public record CardOpenNotify(CardSerializer card) implements Packet<ClientPlayPacketListener> {
    public static final StreamCodec<IntelligentByteBuf, CardOpenNotify> STREAM_CODEC = CardSerializer.STREAM_CODEC.xmap(CardOpenNotify::new, CardOpenNotify::card);

    @Override
    public PacketType<CardOpenNotify> type() {
        return PlayPacketTypes.CARD_OPEN;
    }

    @Override
    public void handle(ClientPlayPacketListener listener) {
        listener.handleCardOpen(this);
    }
}
