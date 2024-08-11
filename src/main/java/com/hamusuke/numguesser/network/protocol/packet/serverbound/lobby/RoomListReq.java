package com.hamusuke.numguesser.network.protocol.packet.serverbound.lobby;

import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.listener.server.lobby.ServerLobbyPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;

public record RoomListReq() implements Packet<ServerLobbyPacketListener> {
    public RoomListReq(IntelligentByteBuf buf) {
        this();
    }

    @Override
    public void write(IntelligentByteBuf buf) {
    }

    @Override
    public void handle(ServerLobbyPacketListener listener) {
        listener.handleRoomList(this);
    }
}
