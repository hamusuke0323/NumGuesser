package com.hamusuke.numguesser.network.protocol.packet.play.clientbound;

import com.hamusuke.numguesser.game.card.Card.CardSerializer;
import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.codec.StreamCodec;
import com.hamusuke.numguesser.network.listener.client.main.ClientPlayPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import com.hamusuke.numguesser.network.protocol.packet.PacketType;
import com.hamusuke.numguesser.network.protocol.packet.play.PlayPacketTypes;

public record PlayerStartAttackNotify(CardSerializer card,
                                      boolean cancellable) implements Packet<ClientPlayPacketListener> {
    public static final StreamCodec<IntelligentByteBuf, PlayerStartAttackNotify> STREAM_CODEC = Packet.codec(PlayerStartAttackNotify::write, PlayerStartAttackNotify::new);

    private PlayerStartAttackNotify(IntelligentByteBuf buf) {
        this(new CardSerializer(buf), buf.readBoolean());
    }

    private void write(IntelligentByteBuf buf) {
        this.card.writeTo(buf);
        buf.writeBoolean(this.cancellable);
    }

    @Override
    public PacketType<PlayerStartAttackNotify> type() {
        return PlayPacketTypes.PLAYER_START_ATTACK;
    }

    @Override
    public void handle(ClientPlayPacketListener listener) {
        listener.handlePlayerStartAttacking(this);
    }
}
