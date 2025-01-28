package com.hamusuke.numguesser.network.protocol.packet.lobby.serverbound;

import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.codec.StreamCodec;
import com.hamusuke.numguesser.network.listener.server.lobby.ServerLobbyPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import com.hamusuke.numguesser.network.protocol.packet.PacketType;
import com.hamusuke.numguesser.network.protocol.packet.lobby.LobbyPacketTypes;
import com.hamusuke.numguesser.room.Room;

public record RoomListQueryReq(String query) implements Packet<ServerLobbyPacketListener> {
    public static final StreamCodec<IntelligentByteBuf, RoomListQueryReq> STREAM_CODEC = Packet.codec(RoomListQueryReq::write, RoomListQueryReq::new);

    private RoomListQueryReq(IntelligentByteBuf buf) {
        this(buf.readString(Room.MAX_ROOM_NAME_LENGTH));
    }

    private void write(IntelligentByteBuf buf) {
        buf.writeString(this.query, Room.MAX_ROOM_NAME_LENGTH);
    }

    @Override
    public PacketType<RoomListQueryReq> type() {
        return LobbyPacketTypes.ROOM_LIST_QUERY;
    }

    @Override
    public void handle(ServerLobbyPacketListener listener) {
        listener.handleRoomListQuery(this);
    }
}
