package com.hamusuke.numguesser.network.listener.client.lobby;

import com.hamusuke.numguesser.network.listener.PacketListener;
import com.hamusuke.numguesser.network.protocol.packet.clientbound.lobby.*;

public interface ClientLobbyPacketListener extends PacketListener {
    void handlePong(LobbyPongRsp packet);

    void handleDisconnectPacket(LobbyDisconnectNotify packet);

    void handleRoomList(RoomListNotify packet);

    void handleJoinRoomSucc(JoinRoomSuccNotify packet);

    void handleJoinRoomFail(JoinRoomFailNotify packet);

    void handleEnterPassword(EnterPasswordReq packet);
}
