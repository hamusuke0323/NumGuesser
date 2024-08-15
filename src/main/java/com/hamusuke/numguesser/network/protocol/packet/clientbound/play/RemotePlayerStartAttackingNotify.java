package com.hamusuke.numguesser.network.protocol.packet.clientbound.play;

import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.listener.client.main.ClientPlayPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;

public record RemotePlayerStartAttackingNotify(int id) implements Packet<ClientPlayPacketListener> {
    public RemotePlayerStartAttackingNotify(IntelligentByteBuf buf) {
        this(buf.readVarInt());
    }

    @Override
    public void write(IntelligentByteBuf buf) {
        buf.writeVarInt(this.id);
    }

    @Override
    public void handle(ClientPlayPacketListener listener) {
        listener.handleRemotePlayerStartAttacking(this);
    }
}
