package com.hamusuke.numguesser.network.protocol.packet.play.clientbound;

import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.codec.StreamCodec;
import com.hamusuke.numguesser.network.listener.client.main.ClientPlayPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import com.hamusuke.numguesser.network.protocol.packet.PacketType;
import com.hamusuke.numguesser.network.protocol.packet.play.PlayPacketTypes;

public record PlayerCardSelectionSyncNotify(int playerId, int cardId) implements Packet<ClientPlayPacketListener> {
    public static final StreamCodec<IntelligentByteBuf, PlayerCardSelectionSyncNotify> STREAM_CODEC = Packet.codec(PlayerCardSelectionSyncNotify::write, PlayerCardSelectionSyncNotify::new);

    private PlayerCardSelectionSyncNotify(IntelligentByteBuf buf) {
        this(buf.readVarInt(), buf.readVarInt());
    }

    private void write(IntelligentByteBuf buf) {
        buf.writeVarInt(this.playerId);
        buf.writeVarInt(this.cardId);
    }

    @Override
    public PacketType<PlayerCardSelectionSyncNotify> type() {
        return PlayPacketTypes.PLAYER_CARD_SELECTION_SYNC;
    }

    @Override
    public void handle(ClientPlayPacketListener listener) {
        listener.handlePlayerCardSelectionSync(this);
    }
}
