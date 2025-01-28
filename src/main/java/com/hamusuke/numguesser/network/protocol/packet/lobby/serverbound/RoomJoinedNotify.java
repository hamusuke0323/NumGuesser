package com.hamusuke.numguesser.network.protocol.packet.lobby.serverbound;

import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.codec.StreamCodec;
import com.hamusuke.numguesser.network.listener.server.lobby.ServerLobbyPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import com.hamusuke.numguesser.network.protocol.packet.PacketType;
import com.hamusuke.numguesser.network.protocol.packet.lobby.LobbyPacketTypes;

public class RoomJoinedNotify implements Packet<ServerLobbyPacketListener> {
    public static final RoomJoinedNotify INSTANCE = new RoomJoinedNotify();
    public static final StreamCodec<IntelligentByteBuf, RoomJoinedNotify> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    private RoomJoinedNotify() {
    }

    @Override
    public PacketType<RoomJoinedNotify> type() {
        return LobbyPacketTypes.ROOM_JOINED;
    }

    @Override
    public void handle(ServerLobbyPacketListener listener) {
        listener.handleRoomJoined(this);
    }

    @Override
    public boolean isTerminal() {
        return true;
    }
}
