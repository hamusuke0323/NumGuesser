package com.hamusuke.numguesser.network.protocol.packet.clientbound.play;

import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.listener.client.main.ClientPlayPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;

public record TossOrAttackSelectionNotify() implements Packet<ClientPlayPacketListener> {
    public TossOrAttackSelectionNotify(IntelligentByteBuf buf) {
        this();
    }

    @Override
    public void write(IntelligentByteBuf buf) {
    }

    @Override
    public void handle(ClientPlayPacketListener listener) {
        listener.handleTossOrAttackSelection(this);
    }
}
