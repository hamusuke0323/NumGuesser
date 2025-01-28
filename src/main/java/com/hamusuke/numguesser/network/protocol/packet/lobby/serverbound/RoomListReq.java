package com.hamusuke.numguesser.network.protocol.packet.lobby.serverbound;

import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.codec.StreamCodec;
import com.hamusuke.numguesser.network.listener.server.lobby.ServerLobbyPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import com.hamusuke.numguesser.network.protocol.packet.PacketType;
import com.hamusuke.numguesser.network.protocol.packet.lobby.LobbyPacketTypes;

public class RoomListReq implements Packet<ServerLobbyPacketListener> {
    public static final RoomListReq INSTANCE = new RoomListReq();
    public static final StreamCodec<IntelligentByteBuf, RoomListReq> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    private RoomListReq() {
    }

    @Override
    public PacketType<RoomListReq> type() {
        return LobbyPacketTypes.ROOM_LIST_REQ;
    }

    @Override
    public void handle(ServerLobbyPacketListener listener) {
        listener.handleRoomList(this);
    }
}
