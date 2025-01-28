package com.hamusuke.numguesser.network.protocol.packet.lobby.clientbound;

import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.codec.StreamCodec;
import com.hamusuke.numguesser.network.listener.client.lobby.ClientLobbyPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import com.hamusuke.numguesser.network.protocol.packet.PacketType;
import com.hamusuke.numguesser.network.protocol.packet.lobby.LobbyPacketTypes;

public record JoinRoomFailNotify(String msg) implements Packet<ClientLobbyPacketListener> {
    public static final StreamCodec<IntelligentByteBuf, JoinRoomFailNotify> STREAM_CODEC = Packet.codec(JoinRoomFailNotify::write, JoinRoomFailNotify::new);

    private JoinRoomFailNotify(IntelligentByteBuf buf) {
        this(buf.readString());
    }

    private void write(IntelligentByteBuf buf) {
        buf.writeString(this.msg);
    }

    @Override
    public PacketType<JoinRoomFailNotify> type() {
        return LobbyPacketTypes.JOIN_ROOM_FAIL;
    }

    @Override
    public void handle(ClientLobbyPacketListener listener) {
        listener.handleJoinRoomFail(this);
    }
}
