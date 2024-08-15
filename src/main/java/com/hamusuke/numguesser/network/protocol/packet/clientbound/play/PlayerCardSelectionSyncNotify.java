package com.hamusuke.numguesser.network.protocol.packet.clientbound.play;

import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.listener.client.main.ClientPlayPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;

public record PlayerCardSelectionSyncNotify(int playerId, int cardId) implements Packet<ClientPlayPacketListener> {
    public PlayerCardSelectionSyncNotify(IntelligentByteBuf buf) {
        this(buf.readVarInt(), buf.readVarInt());
    }

    @Override
    public void write(IntelligentByteBuf buf) {
        buf.writeVarInt(this.playerId);
        buf.writeVarInt(this.cardId);
    }

    @Override
    public void handle(ClientPlayPacketListener listener) {
        listener.handlePlayerCardSelectionSync(this);
    }
}
