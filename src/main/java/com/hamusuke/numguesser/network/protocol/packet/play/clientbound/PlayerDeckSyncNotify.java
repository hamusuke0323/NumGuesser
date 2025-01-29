package com.hamusuke.numguesser.network.protocol.packet.play.clientbound;

import com.google.common.collect.ImmutableList;
import com.hamusuke.numguesser.game.card.CardSerializer;
import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.codec.StreamCodec;
import com.hamusuke.numguesser.network.listener.client.main.ClientPlayPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import com.hamusuke.numguesser.network.protocol.packet.PacketType;
import com.hamusuke.numguesser.network.protocol.packet.play.PlayPacketTypes;

import java.util.List;

public record PlayerDeckSyncNotify(int id, List<CardSerializer> cards) implements Packet<ClientPlayPacketListener> {
    public static final StreamCodec<IntelligentByteBuf, PlayerDeckSyncNotify> STREAM_CODEC = Packet.codec(PlayerDeckSyncNotify::write, PlayerDeckSyncNotify::new);

    private PlayerDeckSyncNotify(IntelligentByteBuf buf) {
        this(buf.readVarInt(), buf.readList(CardSerializer.STREAM_CODEC::decode, ImmutableList::copyOf));
    }

    private void write(IntelligentByteBuf buf) {
        buf.writeVarInt(this.id);
        buf.writeList(this.cards, CardSerializer.STREAM_CODEC::encode);
    }

    @Override
    public PacketType<PlayerDeckSyncNotify> type() {
        return PlayPacketTypes.PLAYER_DECK_SYNC;
    }

    @Override
    public void handle(ClientPlayPacketListener listener) {
        listener.handlePlayerDeckSync(this);
    }
}
