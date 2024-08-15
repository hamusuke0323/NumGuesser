package com.hamusuke.numguesser.network.protocol.packet.clientbound.play;

import com.hamusuke.numguesser.game.card.Card.CardSerializer;
import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.listener.client.main.ClientPlayPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;

public record PlayerStartAttackingNotify(int id, CardSerializer card) implements Packet<ClientPlayPacketListener> {
    public PlayerStartAttackingNotify(IntelligentByteBuf buf) {
        this(buf.readVarInt(), new CardSerializer(buf));
    }

    @Override
    public void write(IntelligentByteBuf buf) {
        buf.writeVarInt(this.id);
        this.card.writeTo(buf);
    }

    @Override
    public void handle(ClientPlayPacketListener listener) {
        listener.handlePlayerStartAttacking(this);
    }
}
