package com.hamusuke.numguesser.server.room;

import com.google.common.collect.Lists;
import com.hamusuke.numguesser.game.NumGuesserGame;
import com.hamusuke.numguesser.network.Player;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import com.hamusuke.numguesser.network.protocol.packet.clientbound.common.ChatNotify;
import com.hamusuke.numguesser.network.protocol.packet.clientbound.common.PlayerJoinNotify;
import com.hamusuke.numguesser.network.protocol.packet.clientbound.common.PlayerLeaveNotify;
import com.hamusuke.numguesser.network.protocol.packet.clientbound.lobby.JoinRoomSuccNotify;
import com.hamusuke.numguesser.network.protocol.packet.clientbound.play.ExitGameSuccNotify;
import com.hamusuke.numguesser.network.protocol.packet.clientbound.room.StartGameNotify;
import com.hamusuke.numguesser.room.Room;
import com.hamusuke.numguesser.room.RoomInfo;
import com.hamusuke.numguesser.server.NumGuesserServer;
import com.hamusuke.numguesser.server.network.ServerPlayer;
import com.hamusuke.numguesser.server.network.listener.main.ServerPlayPacketListenerImpl;
import com.hamusuke.numguesser.server.network.listener.main.ServerRoomPacketListenerImpl;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class ServerRoom extends Room {
    private final NumGuesserServer server;
    private final List<ServerPlayer> players = Collections.synchronizedList(Lists.newArrayList());
    private final List<ServerPlayer> playerList;
    private final String password;
    private NumGuesserGame game;

    public ServerRoom(NumGuesserServer server, String roomName, String password) {
        super(roomName);
        this.server = server;
        this.password = password;
        this.playerList = Collections.unmodifiableList(this.players);
    }

    public RoomInfo toInfo() {
        return new RoomInfo(this.id, this.roomName, this.getPlayers().size(), this.hasPassword());
    }

    @Override
    public void tick() {
        super.tick();

        if (this.game != null && this.game.getPlayingPlayers().isEmpty()) {
            this.players.forEach(player -> player.setReady(false));
            this.game = null;
        }
    }

    public void onPlayerReady() {
        if (this.game == null) {
            synchronized (this.players) {
                if (this.isPlayerNumValid() && this.players.stream().allMatch(Player::isReady)) {
                    this.players.forEach(player -> player.setReady(false));

                    this.sendPacketToAllInRoom(new ChatNotify("ゲームを開始します"));
                    this.game = new NumGuesserGame(this.players);
                    this.players.forEach(player -> {
                        player.sendPacket(new StartGameNotify());
                        new ServerPlayPacketListenerImpl(this.server, player.connection.getConnection(), player);
                    });

                    this.game.startGame();
                }
            }
        }
    }

    private boolean isPlayerNumValid() {
        return this.players.size() > 1 && this.players.size() < 5;
    }

    @Override
    public synchronized void join(Player player) {
        var serverPlayer = (ServerPlayer) player;
        serverPlayer.curRoom = this;
        serverPlayer.sendPacket(new JoinRoomSuccNotify(this.toInfo()));
        new ServerRoomPacketListenerImpl(this.server, serverPlayer.connection.getConnection(), serverPlayer);

        this.sendPacketToAllInRoom(new PlayerJoinNotify(serverPlayer));
        this.players.forEach(player1 -> serverPlayer.sendPacket(new PlayerJoinNotify(player1)));
        this.players.add(serverPlayer);

        this.sendPacketToAllInRoom(new ChatNotify("%s が部屋に参加しました".formatted(serverPlayer.getDisplayName())));
    }

    @Override
    public synchronized void leave(Player player) {
        var serverPlayer = (ServerPlayer) player;
        serverPlayer.curRoom = null;
        this.players.remove(serverPlayer);

        if (this.game != null) {
            this.game.leavePlayer(serverPlayer);
        }

        if (this.players.isEmpty()) {
            this.server.removeRoom(this);
            return;
        }

        this.sendPacketToAllInRoom(new PlayerLeaveNotify(serverPlayer));
        this.sendPacketToAllInRoom(new ChatNotify("%s が部屋から退出しました".formatted(serverPlayer.getDisplayName())));
    }

    @Override
    public List<ServerPlayer> getPlayers() {
        return this.playerList;
    }

    public void sendPacketToAllInRoom(Packet<?> packet) {
        this.players.forEach(s -> s.sendPacket(packet));
    }

    public void sendPacketToOthersInRoom(ServerPlayer sender, Packet<?> packet) {
        this.players.stream()
                .filter(player -> player != sender)
                .forEach(player -> player.sendPacket(packet));
    }

    public boolean hasPassword() {
        return !this.password.isEmpty();
    }

    public String getPassword() {
        return this.password;
    }

    public boolean doesPlayerExist(String name) {
        return this.players.stream().anyMatch(player -> player.getName().equals(name));
    }

    public synchronized void exitGame(ServerPlayer player) {
        if (this.game != null) {
            this.game.leavePlayer(player);
            player.sendPacket(new ExitGameSuccNotify());
            new ServerRoomPacketListenerImpl(this.server, player.connection.getConnection(), player);
        }
    }

    public NumGuesserGame getGame() {
        return this.game;
    }

    @Nullable
    public ServerPlayer getPlayer(int id) {
        return (ServerPlayer) super.getPlayer(id);
    }
}
