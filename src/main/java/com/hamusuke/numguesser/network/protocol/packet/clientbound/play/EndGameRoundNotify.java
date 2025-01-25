package com.hamusuke.numguesser.network.protocol.packet.clientbound.play;

import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.listener.client.main.ClientPlayPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;

public record EndGameRoundNotify(boolean isFinalRound) implements Packet<ClientPlayPacketListener> {
    public EndGameRoundNotify(IntelligentByteBuf buf) {
        this(buf.readBoolean());
    }

    @Override
    public void write(IntelligentByteBuf buf) {
        buf.writeBoolean(this.isFinalRound);
    }

    @Override
    public void handle(ClientPlayPacketListener listener) {
        listener.handleEndGameRound(this);
    }
}
