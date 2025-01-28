package com.hamusuke.numguesser.network.protocol.packet.lobby.serverbound;

import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.codec.StreamCodec;
import com.hamusuke.numguesser.network.listener.server.lobby.ServerLobbyPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import com.hamusuke.numguesser.network.protocol.packet.PacketType;
import com.hamusuke.numguesser.network.protocol.packet.lobby.LobbyPacketTypes;

public record JoinRoomReq(int id) implements Packet<ServerLobbyPacketListener> {
    public static final StreamCodec<IntelligentByteBuf, JoinRoomReq> STREAM_CODEC = Packet.codec(JoinRoomReq::write, JoinRoomReq::new);

    private JoinRoomReq(IntelligentByteBuf buf) {
        this(buf.readVarInt());
    }

    private void write(IntelligentByteBuf buf) {
        buf.writeVarInt(this.id);
    }

    @Override
    public PacketType<JoinRoomReq> type() {
        return LobbyPacketTypes.JOIN_ROOM;
    }

    @Override
    public void handle(ServerLobbyPacketListener listener) {
        listener.handleJoinRoom(this);
    }
}
