package com.hamusuke.numguesser.network.listener.client.main;

import com.hamusuke.numguesser.network.listener.TickablePacketListener;
import com.hamusuke.numguesser.network.listener.client.ClientboundBasePacketListener;
import com.hamusuke.numguesser.network.protocol.packet.common.clientbound.*;
import com.hamusuke.numguesser.network.protocol.packet.disconnect.clientbound.DisconnectNotify;

public interface ClientCommonPacketListener extends ClientboundBasePacketListener, TickablePacketListener {
    void handleRoomOwnerChange(RoomOwnerChangeNotify packet);

    void handleGameModeChange(GameModeChangeNotify packet);

    void handleChatPacket(ChatNotify packet);

    void handleDisconnectPacket(DisconnectNotify packet);

    void handleJoinPacket(PlayerJoinNotify packet);

    void handleLeavePacket(PlayerLeaveNotify packet);

    void handleLeaveRoomSucc(LeaveRoomSuccNotify packet);

    void handlePlayerReadySync(PlayerReadySyncNotify packet);

    void handleReadyRsp(ReadyRsp packet);

    void handlePlayerTipPointSync(PlayerTipPointSyncNotify packet);
}
