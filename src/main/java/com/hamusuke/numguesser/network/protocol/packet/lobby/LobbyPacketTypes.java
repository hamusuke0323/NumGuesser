package com.hamusuke.numguesser.network.protocol.packet.lobby;

import com.hamusuke.numguesser.network.listener.client.lobby.ClientLobbyPacketListener;
import com.hamusuke.numguesser.network.listener.server.lobby.ServerLobbyPacketListener;
import com.hamusuke.numguesser.network.protocol.PacketDirection;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import com.hamusuke.numguesser.network.protocol.packet.PacketType;
import com.hamusuke.numguesser.network.protocol.packet.lobby.clientbound.*;
import com.hamusuke.numguesser.network.protocol.packet.lobby.serverbound.*;

public class LobbyPacketTypes {
    public static final PacketType<EnterPasswordReq> ENTER_PASSWORD_REQ = createClientbound("enter_password_req");
    public static final PacketType<JoinRoomFailNotify> JOIN_ROOM_FAIL = createClientbound("join_room_fail");
    public static final PacketType<JoinRoomSuccNotify> JOIN_ROOM_SUCC = createClientbound("join_room_succ");
    public static final PacketType<LobbyDisconnectNotify> DISCONNECT = createClientbound("disconnect");
    public static final PacketType<RoomListNotify> ROOM_LIST = createClientbound("room_list");
    public static final PacketType<CreateRoomReq> CREATE_ROOM = createServerbound("create_room");
    public static final PacketType<EnterPasswordRsp> ENTER_PASSWORD_RSP = createServerbound("enter_password_rsp");
    public static final PacketType<JoinRoomReq> JOIN_ROOM = createServerbound("join_room");
    public static final PacketType<LobbyDisconnectReq> DISCONNECT_REQ = createServerbound("disconnect_req");
    public static final PacketType<RoomJoinedNotify> ROOM_JOINED = createServerbound("room_joined");
    public static final PacketType<RoomListQueryReq> ROOM_LIST_QUERY = createServerbound("room_list_query");
    public static final PacketType<RoomListReq> ROOM_LIST_REQ = createServerbound("room_list_req");

    private static <T extends Packet<ServerLobbyPacketListener>> PacketType<T> createServerbound(String id) {
        return new PacketType<>(PacketDirection.SERVERBOUND, id);
    }

    private static <T extends Packet<ClientLobbyPacketListener>> PacketType<T> createClientbound(String id) {
        return new PacketType<>(PacketDirection.CLIENTBOUND, id);
    }
}
