package com.hamusuke.numguesser.network.protocol.packet.play.clientbound;

import com.hamusuke.numguesser.game.card.CardSerializer;
import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.codec.StreamCodec;
import com.hamusuke.numguesser.network.listener.client.main.ClientPlayPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import com.hamusuke.numguesser.network.protocol.packet.PacketType;
import com.hamusuke.numguesser.network.protocol.packet.play.PlayPacketTypes;

public record PlayerNewCardAddNotify(int id, int index,
                                     CardSerializer card) implements Packet<ClientPlayPacketListener> {
    public static final StreamCodec<IntelligentByteBuf, PlayerNewCardAddNotify> STREAM_CODEC = Packet.codec(PlayerNewCardAddNotify::write, PlayerNewCardAddNotify::new);

    private PlayerNewCardAddNotify(IntelligentByteBuf buf) {
        this(buf.readVarInt(), buf.readVarInt(), CardSerializer.STREAM_CODEC.decode(buf));
    }

    private void write(IntelligentByteBuf buf) {
        buf.writeVarInt(this.id);
        buf.writeVarInt(this.index);
        CardSerializer.STREAM_CODEC.encode(buf, this.card);
    }

    @Override
    public PacketType<PlayerNewCardAddNotify> type() {
        return PlayPacketTypes.PLAYER_NEW_CARD_ADD;
    }

    @Override
    public void handle(ClientPlayPacketListener listener) {
        listener.handlePlayerNewCardAdd(this);
    }
}
