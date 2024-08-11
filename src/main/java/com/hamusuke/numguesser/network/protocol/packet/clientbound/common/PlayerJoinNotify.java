package com.hamusuke.numguesser.network.protocol.packet.clientbound.common;

import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.listener.client.main.ClientCommonPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import com.hamusuke.numguesser.server.network.ServerPlayer;

public record PlayerJoinNotify(int id, String name) implements Packet<ClientCommonPacketListener> {
    public PlayerJoinNotify(ServerPlayer player) {
        this(player.getId(), player.getName());
    }

    public PlayerJoinNotify(IntelligentByteBuf byteBuf) {
        this(byteBuf.readVarInt(), byteBuf.readString());
    }

    @Override
    public void write(IntelligentByteBuf byteBuf) {
        byteBuf.writeVarInt(this.id);
        byteBuf.writeString(this.name);
    }

    @Override
    public void handle(ClientCommonPacketListener listener) {
        listener.handleJoinPacket(this);
    }
}
