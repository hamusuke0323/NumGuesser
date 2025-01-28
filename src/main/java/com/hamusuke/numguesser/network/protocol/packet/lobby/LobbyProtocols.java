package com.hamusuke.numguesser.network.protocol.packet.lobby;

import com.hamusuke.numguesser.network.listener.client.lobby.ClientLobbyPacketListener;
import com.hamusuke.numguesser.network.listener.server.lobby.ServerLobbyPacketListener;
import com.hamusuke.numguesser.network.protocol.Protocol;
import com.hamusuke.numguesser.network.protocol.ProtocolInfo;
import com.hamusuke.numguesser.network.protocol.ProtocolInfoBuilder;
import com.hamusuke.numguesser.network.protocol.packet.lobby.clientbound.*;
import com.hamusuke.numguesser.network.protocol.packet.lobby.serverbound.*;

public class LobbyProtocols {
    public static final ProtocolInfo<ServerLobbyPacketListener> SERVERBOUND = ProtocolInfoBuilder
            .serverboundProtocol(Protocol.LOBBY, builder -> {
                builder.addPacket(LobbyPacketTypes.CREATE_ROOM, CreateRoomReq.STREAM_CODEC)
                        .addPacket(LobbyPacketTypes.ENTER_PASSWORD_RSP, EnterPasswordRsp.STREAM_CODEC)
                        .addPacket(LobbyPacketTypes.JOIN_ROOM, JoinRoomReq.STREAM_CODEC)
                        .addPacket(LobbyPacketTypes.DISCONNECT_REQ, LobbyDisconnectReq.STREAM_CODEC)
                        .addPacket(LobbyPacketTypes.PING, LobbyPingReq.STREAM_CODEC)
                        .addPacket(LobbyPacketTypes.ROOM_JOINED, RoomJoinedNotify.STREAM_CODEC)
                        .addPacket(LobbyPacketTypes.ROOM_LIST_QUERY, RoomListQueryReq.STREAM_CODEC)
                        .addPacket(LobbyPacketTypes.ROOM_LIST_REQ, RoomListReq.STREAM_CODEC);
            });
    public static final ProtocolInfo<ClientLobbyPacketListener> CLIENTBOUND = ProtocolInfoBuilder
            .clientboundProtocol(Protocol.LOBBY, builder -> {
                builder.addPacket(LobbyPacketTypes.ENTER_PASSWORD_REQ, EnterPasswordReq.STREAM_CODEC)
                        .addPacket(LobbyPacketTypes.JOIN_ROOM_FAIL, JoinRoomFailNotify.STREAM_CODEC)
                        .addPacket(LobbyPacketTypes.JOIN_ROOM_SUCC, JoinRoomSuccNotify.STREAM_CODEC)
                        .addPacket(LobbyPacketTypes.DISCONNECT, LobbyDisconnectNotify.STREAM_CODEC)
                        .addPacket(LobbyPacketTypes.PONG, LobbyPongRsp.STREAM_CODEC)
                        .addPacket(LobbyPacketTypes.ROOM_LIST, RoomListNotify.STREAM_CODEC);
            });
}
