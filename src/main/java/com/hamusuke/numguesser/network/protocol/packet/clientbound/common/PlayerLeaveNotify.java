package com.hamusuke.numguesser.network.protocol.packet.clientbound.common;

import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.listener.client.main.ClientCommonPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import com.hamusuke.numguesser.server.network.ServerPlayer;

public record PlayerLeaveNotify(int id) implements Packet<ClientCommonPacketListener> {
    public PlayerLeaveNotify(IntelligentByteBuf byteBuf) {
        this(byteBuf.readVarInt());
    }

    public PlayerLeaveNotify(ServerPlayer player) {
        this(player.getId());
    }

    @Override
    public void write(IntelligentByteBuf byteBuf) {
        byteBuf.writeVarInt(this.id);
    }

    @Override
    public void handle(ClientCommonPacketListener listener) {
        listener.handleLeavePacket(this);
    }
}
