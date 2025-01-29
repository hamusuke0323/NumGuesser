package com.hamusuke.numguesser.server.network.listener.lobby;

import com.hamusuke.numguesser.network.channel.Connection;
import com.hamusuke.numguesser.network.listener.TickablePacketListener;
import com.hamusuke.numguesser.network.listener.server.lobby.ServerLobbyPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.lobby.LobbyProtocols;
import com.hamusuke.numguesser.network.protocol.packet.lobby.clientbound.EnterPasswordReq;
import com.hamusuke.numguesser.network.protocol.packet.lobby.clientbound.JoinRoomFailNotify;
import com.hamusuke.numguesser.network.protocol.packet.lobby.clientbound.RoomListNotify;
import com.hamusuke.numguesser.network.protocol.packet.lobby.serverbound.*;
import com.hamusuke.numguesser.network.protocol.packet.loop.clientbound.PingReq;
import com.hamusuke.numguesser.server.NumGuesserServer;
import com.hamusuke.numguesser.server.network.ServerPlayer;
import com.hamusuke.numguesser.server.network.listener.main.ServerRoomPacketListenerImpl;
import com.hamusuke.numguesser.server.room.ServerRoom;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;

public class ServerLobbyPacketListenerImpl implements ServerLobbyPacketListener, TickablePacketListener {
    private static final Logger LOGGER = LogManager.getLogger();
    private final NumGuesserServer server;
    private final Connection connection;
    private final ServerPlayer serverPlayer;
    private int ticks;

    public ServerLobbyPacketListenerImpl(NumGuesserServer server, Connection connection, ServerPlayer serverPlayer) {
        this.server = server;
        this.connection = connection;
        connection.setupInboundProtocol(LobbyProtocols.SERVERBOUND, this);
        this.serverPlayer = serverPlayer;
        this.serverPlayer.connection = this;
    }

    @Override
    public void tick() {
        this.ticks++;
        if (this.ticks % 20 == 0) {
            this.connection.sendPacket(new PingReq(0L));
        }
    }

    @Override
    public void handleDisconnect(LobbyDisconnectReq packet) {
        this.connection.disconnect("");
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
    public void handleRoomJoined(RoomJoinedNotify packet) {
        new ServerRoomPacketListenerImpl(this.server, this.serverPlayer.connection.getConnection(), this.serverPlayer);
    }

    @Override
    public void onDisconnect(String msg) {
        LOGGER.info("{} lost connection", this.connection.getLoggableAddress(true));
        this.server.getPlayerManager().removePlayer(this.serverPlayer);
    }

    @Override
    public boolean isAcceptingMessages() {
        return this.connection.isConnected();
    }

    @Override
    public Connection getConnection() {
        return this.connection;
    }
}
