package com.hamusuke.numguesser.network.protocol.packet.serverbound.lobby;

import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.listener.server.lobby.ServerLobbyPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;

public record CreateRoomReq(String roomName, String password) implements Packet<ServerLobbyPacketListener> {
    public CreateRoomReq(IntelligentByteBuf buf) {
        this(buf.readString(), buf.readString());
    }

    @Override
    public void write(IntelligentByteBuf buf) {
        buf.writeString(this.roomName);
        buf.writeString(this.password);
    }

    @Override
    public void handle(ServerLobbyPacketListener listener) {
        listener.handleCreateRoom(this);
    }
}
