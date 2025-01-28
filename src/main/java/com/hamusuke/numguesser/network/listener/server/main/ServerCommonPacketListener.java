package com.hamusuke.numguesser.network.listener.server.main;

import com.hamusuke.numguesser.network.listener.TickablePacketListener;
import com.hamusuke.numguesser.network.listener.server.ServerboundPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.common.serverbound.*;

public interface ServerCommonPacketListener extends ServerboundPacketListener, TickablePacketListener {
    void handleDisconnect(DisconnectReq packet);

    void handleChatPacket(ChatReq packet);

    void handlePongPacket(PongRsp packet);

    void handleLeaveRoom(LeaveRoomReq packet);

    void handleLeftRoom(LeftRoomNotify packet);

    void handleReady(ReadyReq packet);

    void handleGameModeSelect(GameModeSelectReq packet);
}
