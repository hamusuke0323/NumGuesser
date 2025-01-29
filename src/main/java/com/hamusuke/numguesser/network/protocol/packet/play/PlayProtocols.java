package com.hamusuke.numguesser.network.protocol.packet.play;

import com.hamusuke.numguesser.network.listener.client.main.ClientPlayPacketListener;
import com.hamusuke.numguesser.network.listener.server.main.ServerPlayPacketListener;
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
import com.hamusuke.numguesser.network.protocol.packet.play.clientbound.*;
import com.hamusuke.numguesser.network.protocol.packet.play.serverbound.*;

public class PlayProtocols {
    public static final ProtocolInfo<ServerPlayPacketListener> SERVERBOUND = ProtocolInfoBuilder
            .serverboundProtocol(Protocol.PLAY, builder -> {
                builder.addPacket(PlayPacketTypes.ATTACK_REQ, AttackReq.STREAM_CODEC)
                        .addPacket(PlayPacketTypes.CARD_FOR_ATTACK_SELECT_RSP, CardForAttackSelectRsp.STREAM_CODEC)
                        .addPacket(PlayPacketTypes.CARD_SELECT_REQ, CardSelectReq.STREAM_CODEC)
                        .addPacket(PlayPacketTypes.CLIENT_COMMAND, ClientCommandReq.STREAM_CODEC)
                        .addPacket(PlayPacketTypes.GAME_EXITED, GameExitedNotify.STREAM_CODEC)
                        .addPacket(PlayPacketTypes.PAIR_COLOR_CHANGE_REQ, PairColorChangeReq.STREAM_CODEC)
                        .addPacket(PlayPacketTypes.PAIR_MAKING_DONE, PairMakingDoneReq.STREAM_CODEC)
                        .addPacket(PlayPacketTypes.TOSS_RSP, TossRsp.STREAM_CODEC);

                builder.addPacket(CommonPacketTypes.CHAT_REQ, ChatReq.STREAM_CODEC)
                        .addPacket(CommonPacketTypes.GAME_MODE_SELECT_REQ, GameModeSelectReq.STREAM_CODEC)
                        .addPacket(CommonPacketTypes.LEAVE_ROOM_REQ, LeaveRoomReq.STREAM_CODEC)
                        .addPacket(CommonPacketTypes.LEFT_ROOM, LeftRoomNotify.STREAM_CODEC)
                        .addPacket(CommonPacketTypes.READY_REQ, ReadyReq.STREAM_CODEC);

                builder.addPacket(LoopPacketTypes.PONG, PongRsp.STREAM_CODEC);

                builder.addPacket(DisconnectPacketTypes.DISCONNECT_REQ, DisconnectReq.STREAM_CODEC);
            });
    public static final ProtocolInfo<ClientPlayPacketListener> CLIENTBOUND = ProtocolInfoBuilder
            .clientboundProtocol(Protocol.PLAY, builder -> {
                builder.addPacket(PlayPacketTypes.ATTACK_RSP, AttackRsp.STREAM_CODEC)
                        .addPacket(PlayPacketTypes.ATTACK_SUCC, AttackSuccNotify.STREAM_CODEC)
                        .addPacket(PlayPacketTypes.CARD_FOR_ATTACK_SELECT_REQ, CardForAttackSelectReq.STREAM_CODEC)
                        .addPacket(PlayPacketTypes.CARD_OPEN, CardOpenNotify.STREAM_CODEC)
                        .addPacket(PlayPacketTypes.CARDS_OPEN, CardsOpenNotify.STREAM_CODEC)
                        .addPacket(PlayPacketTypes.END_GAME_ROUND, EndGameRoundNotify.STREAM_CODEC)
                        .addPacket(PlayPacketTypes.EXIT_GAME_SUCC, ExitGameSuccNotify.STREAM_CODEC)
                        .addPacket(PlayPacketTypes.PAIR_COLOR_CHANGE_NOTIFY, PairColorChangeNotify.STREAM_CODEC)
                        .addPacket(PlayPacketTypes.PAIR_MAKING_START, PairMakingStartNotify.STREAM_CODEC)
                        .addPacket(PlayPacketTypes.PLAYER_CARD_SELECTION_SYNC, PlayerCardSelectionSyncNotify.STREAM_CODEC)
                        .addPacket(PlayPacketTypes.PLAYER_DECK_SYNC, PlayerDeckSyncNotify.STREAM_CODEC)
                        .addPacket(PlayPacketTypes.PLAYER_NEW_CARD_ADD, PlayerNewCardAddNotify.STREAM_CODEC)
                        .addPacket(PlayPacketTypes.PLAYER_NEW_DECK, PlayerNewDeckNotify.STREAM_CODEC)
                        .addPacket(PlayPacketTypes.PLAYER_START_ATTACK, PlayerStartAttackNotify.STREAM_CODEC)
                        .addPacket(PlayPacketTypes.REMOTE_PLAYER_SELECT_CARD_FOR_ATTACK, RemotePlayerSelectCardForAttackNotify.STREAM_CODEC)
                        .addPacket(PlayPacketTypes.REMOTE_PLAYER_START_ATTACK, RemotePlayerStartAttackNotify.STREAM_CODEC)
                        .addPacket(PlayPacketTypes.SEATING_ARRANGEMENT, SeatingArrangementNotify.STREAM_CODEC)
                        .addPacket(PlayPacketTypes.START_GAME_ROUND, StartGameRoundNotify.STREAM_CODEC)
                        .addPacket(PlayPacketTypes.TOSS_NOTIFY, TossNotify.STREAM_CODEC)
                        .addPacket(PlayPacketTypes.TOSS_OR_ATTACK_SELECTION, TossOrAttackSelectionNotify.STREAM_CODEC)
                        .addPacket(PlayPacketTypes.TOSS_REQ, TossReq.STREAM_CODEC);

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
