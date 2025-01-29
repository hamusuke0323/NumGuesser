package com.hamusuke.numguesser.network.listener.server.lobby;

import com.hamusuke.numguesser.network.listener.server.ServerboundBasePacketListener;
import com.hamusuke.numguesser.network.protocol.Protocol;
import com.hamusuke.numguesser.network.protocol.packet.lobby.serverbound.*;

public interface ServerLobbyPacketListener extends ServerboundBasePacketListener {
    @Override
    default Protocol protocol() {
        return Protocol.LOBBY;
    }

    void handleRoomList(RoomListReq packet);

    void handleRoomListQuery(RoomListQueryReq packet);

    void handleCreateRoom(CreateRoomReq packet);

    void handleRoomJoined(RoomJoinedNotify packet);

    void handleJoinRoom(JoinRoomReq packet);

    void handleEnterPassword(EnterPasswordRsp packet);
}
