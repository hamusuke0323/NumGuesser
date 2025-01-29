package com.hamusuke.numguesser.network.protocol.packet.lobby.serverbound;

import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.codec.StreamCodec;
import com.hamusuke.numguesser.network.listener.server.lobby.ServerLobbyPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import com.hamusuke.numguesser.network.protocol.packet.PacketType;
import com.hamusuke.numguesser.network.protocol.packet.lobby.LobbyPacketTypes;
import com.hamusuke.numguesser.room.Room;

public record EnterPasswordRsp(int roomId, String password) implements Packet<ServerLobbyPacketListener> {
    public static final StreamCodec<IntelligentByteBuf, EnterPasswordRsp> STREAM_CODEC = Packet.codec(EnterPasswordRsp::write, EnterPasswordRsp::new);

    private EnterPasswordRsp(IntelligentByteBuf buf) {
        this(buf.readVarInt(), buf.readString(Room.MAX_ROOM_PASSWD_LENGTH));
    }

    private void write(IntelligentByteBuf buf) {
        buf.writeVarInt(this.roomId);
        buf.writeString(this.password, Room.MAX_ROOM_PASSWD_LENGTH);
    }

    @Override
    public PacketType<EnterPasswordRsp> type() {
        return LobbyPacketTypes.ENTER_PASSWORD_RSP;
    }

    @Override
    public void handle(ServerLobbyPacketListener listener) {
        listener.handleEnterPassword(this);
    }
}
