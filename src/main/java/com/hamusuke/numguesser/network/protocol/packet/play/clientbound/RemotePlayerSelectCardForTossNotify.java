package com.hamusuke.numguesser.network.protocol.packet.play.clientbound;

import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.codec.StreamCodec;
import com.hamusuke.numguesser.network.listener.client.main.ClientPlayPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import com.hamusuke.numguesser.network.protocol.packet.PacketType;
import com.hamusuke.numguesser.network.protocol.packet.play.PlayPacketTypes;

public record RemotePlayerSelectCardForTossNotify(int id) implements Packet<ClientPlayPacketListener> {
    public static final StreamCodec<IntelligentByteBuf, RemotePlayerSelectCardForTossNotify> STREAM_CODEC = StreamCodec.ofMember(RemotePlayerSelectCardForTossNotify::write, RemotePlayerSelectCardForTossNotify::new);

    private RemotePlayerSelectCardForTossNotify(IntelligentByteBuf buf) {
        this(buf.readVarInt());
    }

    private void write(IntelligentByteBuf buf) {
        buf.writeVarInt(this.id);
    }

    @Override
    public void handle(ClientPlayPacketListener listener) {
        listener.handleRemotePlayerSelectCardForToss(this);
    }

    @Override
    public PacketType<RemotePlayerSelectCardForTossNotify> type() {
        return PlayPacketTypes.REMOTE_PLAYER_SELECT_CARD_FOR_TOSS;
    }
}
