package com.hamusuke.numguesser.network.listener.client.main;

import com.hamusuke.numguesser.network.listener.PacketListener;
import com.hamusuke.numguesser.network.protocol.packet.clientbound.common.*;

public interface ClientCommonPacketListener extends PacketListener {
    void handleChatPacket(ChatNotify packet);

    void handlePingPacket(PingReq packet);

    void handleDisconnectPacket(DisconnectNotify packet);

    void handleJoinPacket(PlayerJoinNotify packet);

    void handleRTTPacket(RTTChangeNotify packet);

    void handleLeavePacket(PlayerLeaveNotify packet);

    void handleLeaveRoomSucc(LeaveRoomSuccNotify packet);

    void handlePlayerReadySync(PlayerReadySyncNotify packet);

    void handleReadyRsp(ReadyRsp packet);
}
