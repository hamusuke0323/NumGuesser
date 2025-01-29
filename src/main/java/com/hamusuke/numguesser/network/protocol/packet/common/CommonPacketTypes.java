package com.hamusuke.numguesser.network.protocol.packet.common;

import com.hamusuke.numguesser.network.listener.client.main.ClientCommonPacketListener;
import com.hamusuke.numguesser.network.listener.server.main.ServerCommonPacketListener;
import com.hamusuke.numguesser.network.protocol.PacketDirection;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import com.hamusuke.numguesser.network.protocol.packet.PacketType;
import com.hamusuke.numguesser.network.protocol.packet.common.clientbound.*;
import com.hamusuke.numguesser.network.protocol.packet.common.serverbound.*;

public class CommonPacketTypes {
    public static final PacketType<ChatNotify> CHAT_NOTIFY = createClientbound("chat_notify");
    public static final PacketType<DisconnectNotify> DISCONNECT_NOTIFY = createClientbound("disconnect_notify");
    public static final PacketType<GameModeChangeNotify> GAME_MODE_CHANGE = createClientbound("game_mode_change");
    public static final PacketType<LeaveRoomSuccNotify> LEAVE_ROOM_SUCC = createClientbound("leave_room_succ");
    public static final PacketType<PlayerJoinNotify> PLAYER_JOIN = createClientbound("player_join");
    public static final PacketType<PlayerLeaveNotify> PLAYER_LEAVE = createClientbound("player_leave");
    public static final PacketType<PlayerReadySyncNotify> PLAYER_READY_SYNC = createClientbound("player_ready_sync");
    public static final PacketType<PlayerTipPointSyncNotify> PLAYER_TIP_POINT_SYNC = createClientbound("player_tip_point_sync");
    public static final PacketType<ReadyRsp> READY_RSP = createClientbound("ready_rsp");
    public static final PacketType<RoomOwnerChangeNotify> ROOM_OWNER_CHANGE = createClientbound("room_owner_change");
    public static final PacketType<ChatReq> CHAT_REQ = createServerbound("chat_req");
    public static final PacketType<DisconnectReq> DISCONNECT_REQ = createServerbound("disconnect_req");
    public static final PacketType<GameModeSelectReq> GAME_MODE_SELECT_REQ = createServerbound("game_mode_select_req");
    public static final PacketType<LeaveRoomReq> LEAVE_ROOM_REQ = createServerbound("leave_room_req");
    public static final PacketType<LeftRoomNotify> LEFT_ROOM = createServerbound("left_room");
    public static final PacketType<ReadyReq> READY_REQ = createServerbound("ready_req");

    private static <T extends Packet<ServerCommonPacketListener>> PacketType<T> createServerbound(String id) {
        return new PacketType<>(PacketDirection.SERVERBOUND, id);
    }

    private static <T extends Packet<ClientCommonPacketListener>> PacketType<T> createClientbound(String id) {
        return new PacketType<>(PacketDirection.CLIENTBOUND, id);
    }
}
