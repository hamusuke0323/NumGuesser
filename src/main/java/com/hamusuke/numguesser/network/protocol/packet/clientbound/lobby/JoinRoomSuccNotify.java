package com.hamusuke.numguesser.network.protocol.packet.clientbound.lobby;

import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.listener.client.lobby.ClientLobbyPacketListener;
import com.hamusuke.numguesser.network.protocol.Protocol;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import com.hamusuke.numguesser.room.RoomInfo;

public record JoinRoomSuccNotify(RoomInfo info) implements Packet<ClientLobbyPacketListener> {
    public JoinRoomSuccNotify(IntelligentByteBuf buf) {
        this(new RoomInfo(buf));
    }

    @Override
    public void write(IntelligentByteBuf buf) {
        this.info.writeTo(buf);
    }

    @Override
    public void handle(ClientLobbyPacketListener listener) {
        listener.handleJoinRoomSucc(this);
    }

    @Override
    public Protocol nextProtocol() {
        return Protocol.ROOM;
    }
}
