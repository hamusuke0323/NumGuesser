package com.hamusuke.numguesser.network.protocol.packet.lobby.serverbound;

import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.codec.StreamCodec;
import com.hamusuke.numguesser.network.listener.server.lobby.ServerLobbyPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import com.hamusuke.numguesser.network.protocol.packet.PacketType;
import com.hamusuke.numguesser.network.protocol.packet.lobby.LobbyPacketTypes;

public record CreateRoomReq(String roomName, String password) implements Packet<ServerLobbyPacketListener> {
    public static final StreamCodec<IntelligentByteBuf, CreateRoomReq> STREAM_CODEC = Packet.codec(CreateRoomReq::write, CreateRoomReq::new);

    private CreateRoomReq(IntelligentByteBuf buf) {
        this(buf.readString(), buf.readString());
    }

    private void write(IntelligentByteBuf buf) {
        buf.writeString(this.roomName);
        buf.writeString(this.password);
    }

    @Override
    public PacketType<CreateRoomReq> type() {
        return LobbyPacketTypes.CREATE_ROOM;
    }

    @Override
    public void handle(ServerLobbyPacketListener listener) {
        listener.handleCreateRoom(this);
    }
}
