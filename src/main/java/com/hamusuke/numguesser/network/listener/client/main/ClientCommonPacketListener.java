package com.hamusuke.numguesser.network.listener.client.main;

import com.hamusuke.numguesser.network.listener.client.ClientboundPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.common.clientbound.*;

public interface ClientCommonPacketListener extends ClientboundPacketListener {
    void handleRoomOwnerChange(RoomOwnerChangeNotify packet);

    void handleGameModeChange(GameModeChangeNotify packet);

    void handleChatPacket(ChatNotify packet);

    void handlePingPacket(PingReq packet);

    void handleDisconnectPacket(DisconnectNotify packet);

    void handleJoinPacket(PlayerJoinNotify packet);

    void handleRTTPacket(RTTChangeNotify packet);

    void handleLeavePacket(PlayerLeaveNotify packet);

    void handleLeaveRoomSucc(LeaveRoomSuccNotify packet);

    void handlePlayerReadySync(PlayerReadySyncNotify packet);

    void handleReadyRsp(ReadyRsp packet);

    void handlePlayerTipPointSync(PlayerTipPointSyncNotify packet);
}
