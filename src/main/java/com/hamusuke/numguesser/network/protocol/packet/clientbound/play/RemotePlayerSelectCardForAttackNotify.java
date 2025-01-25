package com.hamusuke.numguesser.network.protocol.packet.clientbound.play;

import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.listener.client.main.ClientPlayPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import com.hamusuke.numguesser.server.network.ServerPlayer;

public record RemotePlayerSelectCardForAttackNotify(int id) implements Packet<ClientPlayPacketListener> {
    public RemotePlayerSelectCardForAttackNotify(ServerPlayer serverPlayer) {
        this(serverPlayer.getId());
    }

    public RemotePlayerSelectCardForAttackNotify(IntelligentByteBuf buf) {
        this(buf.readVarInt());
    }

    @Override
    public void write(IntelligentByteBuf buf) {
        buf.writeVarInt(this.id);
    }

    @Override
    public void handle(ClientPlayPacketListener listener) {
        listener.handleRemotePlayerSelectCardForAttack(this);
    }
}
