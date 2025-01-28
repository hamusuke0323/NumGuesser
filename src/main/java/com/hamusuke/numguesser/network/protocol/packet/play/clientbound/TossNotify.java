package com.hamusuke.numguesser.network.protocol.packet.play.clientbound;

import com.hamusuke.numguesser.game.card.Card.CardSerializer;
import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.codec.StreamCodec;
import com.hamusuke.numguesser.network.listener.client.main.ClientPlayPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import com.hamusuke.numguesser.network.protocol.packet.PacketType;
import com.hamusuke.numguesser.network.protocol.packet.play.PlayPacketTypes;

public record TossNotify(CardSerializer card) implements Packet<ClientPlayPacketListener> {
    public static final StreamCodec<IntelligentByteBuf, TossNotify> STREAM_CODEC = Packet.codec(TossNotify::write, TossNotify::new);

    private TossNotify(IntelligentByteBuf buf) {
        this(new CardSerializer(buf));
    }

    private void write(IntelligentByteBuf buf) {
        this.card.writeTo(buf);
    }

    @Override
    public PacketType<TossNotify> type() {
        return PlayPacketTypes.TOSS_NOTIFY;
    }

    @Override
    public void handle(ClientPlayPacketListener listener) {
        listener.handleTossNotify(this);
    }
}
