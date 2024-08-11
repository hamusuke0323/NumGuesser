package com.hamusuke.numguesser.server.network.listener.lobby;

import com.hamusuke.numguesser.network.channel.Connection;
import com.hamusuke.numguesser.network.listener.server.lobby.ServerLobbyPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.clientbound.lobby.EnterPasswordReq;
import com.hamusuke.numguesser.network.protocol.packet.clientbound.lobby.JoinRoomFailNotify;
import com.hamusuke.numguesser.network.protocol.packet.clientbound.lobby.LobbyPongRsp;
import com.hamusuke.numguesser.network.protocol.packet.clientbound.lobby.RoomListNotify;
import com.hamusuke.numguesser.network.protocol.packet.serverbound.lobby.*;
import com.hamusuke.numguesser.server.NumGuesserServer;
import com.hamusuke.numguesser.server.network.ServerPlayer;
import com.hamusuke.numguesser.server.room.ServerRoom;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;

public class ServerLobbyPacketListenerImpl implements ServerLobbyPacketListener {
    private static final Logger LOGGER = LogManager.getLogger();
    private final NumGuesserServer server;
    private final Connection connection;
    private final ServerPlayer serverPlayer;

    public ServerLobbyPacketListenerImpl(NumGuesserServer server, Connection connection, ServerPlayer serverPlayer) {
        this.server = server;
        this.connection = connection;
        connection.setListener(this);
        this.serverPlayer = serverPlayer;
        this.serverPlayer.connection = this;
    }

    @Override
    public void handleDisconnect(LobbyDisconnectReq packet) {
        this.connection.disconnect("");
    }

    @Override
    public void handlePing(LobbyPingReq packet) {
        this.connection.sendPacket(new LobbyPongRsp());
    }

    @Override
    public void handleJoinRoom(JoinRoomReq packet) {
        var room = this.server.getRoomMap().get(packet.id());
        if (room == null) {
            this.connection.sendPacket(new JoinRoomFailNotify("部屋が見つかりませんでした\n既に削除された可能性があります"));
            return;
        }

        if (room.hasPassword()) {
            this.serverPlayer.sendPacket(new EnterPasswordReq(room.getId(), ""));
            return;
        }

        this.serverPlayer.curRoom = room;
        room.join(this.serverPlayer);
    }

    @Override
    public void handleEnterPassword(EnterPasswordRsp packet) {
        var room = this.server.getRoomMap().get(packet.roomId());
        if (room == null) {
            this.connection.sendPacket(new JoinRoomFailNotify("部屋が見つかりませんでした\n既に削除された可能性があります"));
            return;
        }

        if (room.hasPassword() && !room.getPassword().equals(packet.password())) {
            this.connection.sendPacket(new EnterPasswordReq(room.getId(), "パスワードが間違っています"));
            return;
        }

        this.serverPlayer.curRoom = room;
        room.join(this.serverPlayer);
    }

    @Override
    public void handleRoomList(RoomListReq packet) {
        var rooms = this.server.getRooms();
        if (rooms.isEmpty()) {
            this.connection.sendPacket(new RoomListNotify(Collections.emptyList()));
            return;
        }

        rooms = rooms.subList(0, Math.min(rooms.size(), 10));
        this.connection.sendPacket(new RoomListNotify(rooms.stream().map(ServerRoom::toInfo).toList()));
    }

    @Override
    public void handleRoomListQuery(RoomListQueryReq packet) {
        var list = this.server.getRooms();
        if (list.isEmpty() || packet.query().isEmpty() || packet.query().isBlank()) {
            this.connection.sendPacket(new RoomListNotify(Collections.emptyList()));
            return;
        }

        list = list.stream().filter(serverRoom -> serverRoom.getRoomName().contains(packet.query())).toList();
        this.connection.sendPacket(new RoomListNotify(list.stream().map(ServerRoom::toInfo).toList()));
    }

    @Override
    public void handleCreateRoom(CreateRoomReq packet) {
        this.server.createRoom(this.serverPlayer, packet.roomName(), packet.password());
    }

    @Override
    public void onDisconnected(String msg) {
        LOGGER.info("{} lost connection", this.connection.getAddress());
        this.server.getPlayerManager().removePlayer(this.serverPlayer);
    }

    @Override
    public Connection getConnection() {
        return this.connection;
    }
}
