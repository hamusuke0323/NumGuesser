package com.hamusuke.numguesser.network.protocol.packet.play.clientbound;

import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.codec.StreamCodec;
import com.hamusuke.numguesser.network.listener.client.main.ClientPlayPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import com.hamusuke.numguesser.network.protocol.packet.PacketType;
import com.hamusuke.numguesser.network.protocol.packet.play.PlayPacketTypes;
import com.hamusuke.numguesser.server.network.ServerPlayer;

public record RemotePlayerSelectCardForAttackNotify(int id) implements Packet<ClientPlayPacketListener> {
    public static final StreamCodec<IntelligentByteBuf, RemotePlayerSelectCardForAttackNotify> STREAM_CODEC = Packet.codec(RemotePlayerSelectCardForAttackNotify::write, RemotePlayerSelectCardForAttackNotify::new);

    public RemotePlayerSelectCardForAttackNotify(ServerPlayer serverPlayer) {
        this(serverPlayer.getId());
    }

    private RemotePlayerSelectCardForAttackNotify(IntelligentByteBuf buf) {
        this(buf.readVarInt());
    }

    private void write(IntelligentByteBuf buf) {
        buf.writeVarInt(this.id);
    }

    @Override
    public PacketType<RemotePlayerSelectCardForAttackNotify> type() {
        return PlayPacketTypes.REMOTE_PLAYER_SELECT_CARD_FOR_ATTACK;
    }

    @Override
    public void handle(ClientPlayPacketListener listener) {
        listener.handleRemotePlayerSelectCardForAttack(this);
    }
}
