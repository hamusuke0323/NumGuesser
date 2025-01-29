package com.hamusuke.numguesser.network.protocol.packet.lobby.clientbound;

import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.codec.StreamCodec;
import com.hamusuke.numguesser.network.listener.client.lobby.ClientLobbyPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import com.hamusuke.numguesser.network.protocol.packet.PacketType;
import com.hamusuke.numguesser.network.protocol.packet.lobby.LobbyPacketTypes;
import com.hamusuke.numguesser.room.RoomInfo;

public record JoinRoomSuccNotify(RoomInfo info) implements Packet<ClientLobbyPacketListener> {
    public static final StreamCodec<IntelligentByteBuf, JoinRoomSuccNotify> STREAM_CODEC = Packet.codec(JoinRoomSuccNotify::write, JoinRoomSuccNotify::new);

    private JoinRoomSuccNotify(IntelligentByteBuf buf) {
        this(RoomInfo.STREAM_CODEC.decode(buf));
    }

    private void write(IntelligentByteBuf buf) {
        RoomInfo.STREAM_CODEC.encode(buf, this.info);
    }

    @Override
    public PacketType<JoinRoomSuccNotify> type() {
        return LobbyPacketTypes.JOIN_ROOM_SUCC;
    }

    @Override
    public void handle(ClientLobbyPacketListener listener) {
        listener.handleJoinRoomSucc(this);
    }

    @Override
    public boolean isTerminal() {
        return true;
    }
}
