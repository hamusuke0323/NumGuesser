package com.hamusuke.numguesser.network.protocol.packet.play.clientbound;

import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.codec.StreamCodec;
import com.hamusuke.numguesser.network.listener.client.main.ClientPlayPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import com.hamusuke.numguesser.network.protocol.packet.PacketType;
import com.hamusuke.numguesser.network.protocol.packet.play.PlayPacketTypes;

public record RemotePlayerSelectTossOrAttackNotify(int id) implements Packet<ClientPlayPacketListener> {
    public static final StreamCodec<IntelligentByteBuf, RemotePlayerSelectTossOrAttackNotify> STREAM_CODEC = StreamCodec.ofMember(RemotePlayerSelectTossOrAttackNotify::write, RemotePlayerSelectTossOrAttackNotify::new);

    private RemotePlayerSelectTossOrAttackNotify(IntelligentByteBuf buf) {
        this(buf.readVarInt());
    }

    private void write(IntelligentByteBuf buf) {
        buf.writeVarInt(this.id);
    }

    @Override
    public void handle(ClientPlayPacketListener listener) {
        listener.handleRemotePlayerSelectTossOrAttack(this);
    }

    @Override
    public PacketType<RemotePlayerSelectTossOrAttackNotify> type() {
        return PlayPacketTypes.REMOTE_PLAYER_SELECT_TOSS_OR_ATTACK;
    }
}
