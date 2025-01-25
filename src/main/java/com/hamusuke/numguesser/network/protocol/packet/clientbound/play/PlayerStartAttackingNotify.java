package com.hamusuke.numguesser.network.protocol.packet.clientbound.play;

import com.hamusuke.numguesser.game.card.Card.CardSerializer;
import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.listener.client.main.ClientPlayPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;

public record PlayerStartAttackingNotify(CardSerializer card,
                                         boolean cancellable) implements Packet<ClientPlayPacketListener> {
    public PlayerStartAttackingNotify(IntelligentByteBuf buf) {
        this(new CardSerializer(buf), buf.readBoolean());
    }

    @Override
    public void write(IntelligentByteBuf buf) {
        this.card.writeTo(buf);
        buf.writeBoolean(this.cancellable);
    }

    @Override
    public void handle(ClientPlayPacketListener listener) {
        listener.handlePlayerStartAttacking(this);
    }
}
