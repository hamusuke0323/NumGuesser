package com.hamusuke.numguesser.network.protocol.packet.play.clientbound;

import com.hamusuke.numguesser.game.card.Card.CardSerializer;
import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.codec.StreamCodec;
import com.hamusuke.numguesser.network.listener.client.main.ClientPlayPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import com.hamusuke.numguesser.network.protocol.packet.PacketType;
import com.hamusuke.numguesser.network.protocol.packet.play.PlayPacketTypes;

public record RemotePlayerStartAttackNotify(int id,
                                            CardSerializer cardForAttack) implements Packet<ClientPlayPacketListener> {
    public static final StreamCodec<IntelligentByteBuf, RemotePlayerStartAttackNotify> STREAM_CODEC = Packet.codec(RemotePlayerStartAttackNotify::write, RemotePlayerStartAttackNotify::new);

    private RemotePlayerStartAttackNotify(IntelligentByteBuf buf) {
        this(buf.readVarInt(), new CardSerializer(buf));
    }

    private void write(IntelligentByteBuf buf) {
        buf.writeVarInt(this.id);
        this.cardForAttack.writeTo(buf);
    }

    @Override
    public PacketType<RemotePlayerStartAttackNotify> type() {
        return PlayPacketTypes.REMOTE_PLAYER_START_ATTACK;
    }

    @Override
    public void handle(ClientPlayPacketListener listener) {
        listener.handleRemotePlayerStartAttacking(this);
    }
}
