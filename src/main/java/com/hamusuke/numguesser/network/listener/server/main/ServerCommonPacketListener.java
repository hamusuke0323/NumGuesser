package com.hamusuke.numguesser.network.listener.server.main;

import com.hamusuke.numguesser.network.listener.server.ServerPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.serverbound.common.*;

public interface ServerCommonPacketListener extends ServerPacketListener {
    void handleDisconnect(DisconnectReq packet);

    void handleChatPacket(ChatReq packet);

    void handlePongPacket(PongRsp packet);

    void handleLeaveRoom(LeaveRoomReq packet);

    void handleReady(ReadyReq packet);
}
