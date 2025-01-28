package com.hamusuke.numguesser.network.listener.client.lobby;

import com.hamusuke.numguesser.network.listener.client.ClientboundPacketListener;
import com.hamusuke.numguesser.network.protocol.Protocol;
import com.hamusuke.numguesser.network.protocol.packet.lobby.clientbound.*;

public interface ClientLobbyPacketListener extends ClientboundPacketListener {
    @Override
    default Protocol protocol() {
        return Protocol.LOBBY;
    }

    void handlePong(LobbyPongRsp packet);

    void handleDisconnectPacket(LobbyDisconnectNotify packet);

    void handleRoomList(RoomListNotify packet);

    void handleJoinRoomSucc(JoinRoomSuccNotify packet);

    void handleJoinRoomFail(JoinRoomFailNotify packet);

    void handleEnterPassword(EnterPasswordReq packet);
}
