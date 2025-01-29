package com.hamusuke.numguesser.network.listener.server.main;

import com.hamusuke.numguesser.network.listener.TickablePacketListener;
import com.hamusuke.numguesser.network.listener.server.ServerboundBasePacketListener;
import com.hamusuke.numguesser.network.protocol.packet.common.serverbound.*;

public interface ServerCommonPacketListener extends ServerboundBasePacketListener, TickablePacketListener {
    void handleDisconnect(DisconnectReq packet);

    void handleChatPacket(ChatReq packet);

    void handleLeaveRoom(LeaveRoomReq packet);

    void handleLeftRoom(LeftRoomNotify packet);

    void handleReady(ReadyReq packet);

    void handleGameModeSelect(GameModeSelectReq packet);
}
