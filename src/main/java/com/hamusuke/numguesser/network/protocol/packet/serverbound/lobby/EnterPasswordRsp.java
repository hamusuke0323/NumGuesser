package com.hamusuke.numguesser.network.protocol.packet.serverbound.lobby;

import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.listener.server.lobby.ServerLobbyPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import com.hamusuke.numguesser.room.Room;

public record EnterPasswordRsp(int roomId, String password) implements Packet<ServerLobbyPacketListener> {
    public EnterPasswordRsp(IntelligentByteBuf buf) {
        this(buf.readVarInt(), buf.readString(Room.MAX_ROOM_PASSWD_LENGTH));
    }

    @Override
    public void write(IntelligentByteBuf buf) {
        buf.writeVarInt(this.roomId);
        buf.writeString(this.password, Room.MAX_ROOM_PASSWD_LENGTH);
    }

    @Override
    public void handle(ServerLobbyPacketListener listener) {
        listener.handleEnterPassword(this);
    }
}
