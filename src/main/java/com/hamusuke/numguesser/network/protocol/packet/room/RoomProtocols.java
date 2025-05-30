package com.hamusuke.numguesser.network.protocol.packet.room;

import com.hamusuke.numguesser.network.listener.client.main.ClientRoomPacketListener;
import com.hamusuke.numguesser.network.listener.server.main.ServerRoomPacketListener;
import com.hamusuke.numguesser.network.protocol.Protocol;
import com.hamusuke.numguesser.network.protocol.ProtocolInfo;
import com.hamusuke.numguesser.network.protocol.ProtocolInfoBuilder;
import com.hamusuke.numguesser.network.protocol.packet.common.CommonPacketTypes;
import com.hamusuke.numguesser.network.protocol.packet.common.clientbound.*;
import com.hamusuke.numguesser.network.protocol.packet.common.serverbound.*;
import com.hamusuke.numguesser.network.protocol.packet.disconnect.DisconnectPacketTypes;
import com.hamusuke.numguesser.network.protocol.packet.disconnect.clientbound.DisconnectNotify;
import com.hamusuke.numguesser.network.protocol.packet.disconnect.serverbound.DisconnectReq;
import com.hamusuke.numguesser.network.protocol.packet.loop.LoopPacketTypes;
import com.hamusuke.numguesser.network.protocol.packet.loop.clientbound.PingReq;
import com.hamusuke.numguesser.network.protocol.packet.loop.clientbound.RTTChangeNotify;
import com.hamusuke.numguesser.network.protocol.packet.loop.serverbound.PongRsp;
import com.hamusuke.numguesser.network.protocol.packet.room.clientbound.StartGameNotify;
import com.hamusuke.numguesser.network.protocol.packet.room.serverbound.ClientStartedGameNotify;

public class RoomProtocols {
    public static final ProtocolInfo<ServerRoomPacketListener> SERVERBOUND = ProtocolInfoBuilder
            .serverboundProtocol(Protocol.ROOM, builder -> {
                builder.addPacket(RoomPacketTypes.CLIENT_STARTED_GAME, ClientStartedGameNotify.STREAM_CODEC);

                builder.addPacket(CommonPacketTypes.CHAT_REQ, ChatReq.STREAM_CODEC)
                        .addPacket(CommonPacketTypes.GAME_MODE_SELECT_REQ, GameModeSelectReq.STREAM_CODEC)
                        .addPacket(CommonPacketTypes.LEAVE_ROOM_REQ, LeaveRoomReq.STREAM_CODEC)
                        .addPacket(CommonPacketTypes.LEFT_ROOM, LeftRoomNotify.STREAM_CODEC)
                        .addPacket(CommonPacketTypes.READY_REQ, ReadyReq.STREAM_CODEC);

                builder.addPacket(LoopPacketTypes.PONG, PongRsp.STREAM_CODEC);

                builder.addPacket(DisconnectPacketTypes.DISCONNECT_REQ, DisconnectReq.STREAM_CODEC);
            });
    public static final ProtocolInfo<ClientRoomPacketListener> CLIENTBOUND = ProtocolInfoBuilder
            .clientboundProtocol(Protocol.ROOM, builder -> {
                builder.addPacket(RoomPacketTypes.START_GAME, StartGameNotify.STREAM_CODEC);

                builder.addPacket(CommonPacketTypes.CHAT_NOTIFY, ChatNotify.STREAM_CODEC)
                        .addPacket(CommonPacketTypes.GAME_MODE_CHANGE, GameModeChangeNotify.STREAM_CODEC)
                        .addPacket(CommonPacketTypes.LEAVE_ROOM_SUCC, LeaveRoomSuccNotify.STREAM_CODEC)
                        .addPacket(CommonPacketTypes.PLAYER_JOIN, PlayerJoinNotify.STREAM_CODEC)
                        .addPacket(CommonPacketTypes.PLAYER_LEAVE, PlayerLeaveNotify.STREAM_CODEC)
                        .addPacket(CommonPacketTypes.PLAYER_READY_SYNC, PlayerReadySyncNotify.STREAM_CODEC)
                        .addPacket(CommonPacketTypes.PLAYER_TIP_POINT_SYNC, PlayerTipPointSyncNotify.STREAM_CODEC)
                        .addPacket(CommonPacketTypes.READY_RSP, ReadyRsp.STREAM_CODEC)
                        .addPacket(CommonPacketTypes.ROOM_OWNER_CHANGE, RoomOwnerChangeNotify.STREAM_CODEC);

                builder.addPacket(LoopPacketTypes.PING, PingReq.STREAM_CODEC)
                        .addPacket(LoopPacketTypes.RTT_CHANGE, RTTChangeNotify.STREAM_CODEC);

                builder.addPacket(DisconnectPacketTypes.DISCONNECT_NOTIFY, DisconnectNotify.STREAM_CODEC);
            });
}
