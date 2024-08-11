package com.hamusuke.numguesser.network.listener.server.lobby;

import com.hamusuke.numguesser.network.listener.server.ServerPacketListener;
import com.hamusuke.numguesser.network.listener.server.ServerPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.serverbound.lobby.*;

public interface ServerLobbyPacketListener extends ServerPacketListener {
    void handlePing(LobbyPingReq packet);

    void handleDisconnect(LobbyDisconnectReq packet);

    void handleRoomList(RoomListReq packet);

    void handleRoomListQuery(RoomListQueryReq packet);

    void handleCreateRoom(CreateRoomReq packet);

    void handleJoinRoom(JoinRoomReq packet);

    void handleEnterPassword(EnterPasswordRsp packet);
}
