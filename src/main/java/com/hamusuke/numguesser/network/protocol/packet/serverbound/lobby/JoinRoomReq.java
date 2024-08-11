package com.hamusuke.numguesser.network.protocol.packet.serverbound.lobby;

import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.listener.server.lobby.ServerLobbyPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;

public record JoinRoomReq(int id) implements Packet<ServerLobbyPacketListener> {
    public JoinRoomReq(IntelligentByteBuf buf) {
        this(buf.readVarInt());
    }

    @Override
    public void write(IntelligentByteBuf buf) {
        buf.writeVarInt(this.id);
    }

    @Override
    public void handle(ServerLobbyPacketListener listener) {
        listener.handleJoinRoom(this);
    }
}
