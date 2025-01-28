package com.hamusuke.numguesser.server.room;

import com.google.common.collect.Lists;
import com.hamusuke.numguesser.game.GameMode;
import com.hamusuke.numguesser.game.mode.NormalGameMode;
import com.hamusuke.numguesser.network.Player;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import com.hamusuke.numguesser.network.protocol.packet.common.clientbound.*;
import com.hamusuke.numguesser.network.protocol.packet.lobby.clientbound.JoinRoomFailNotify;
import com.hamusuke.numguesser.network.protocol.packet.lobby.clientbound.JoinRoomSuccNotify;
import com.hamusuke.numguesser.network.protocol.packet.play.PlayProtocols;
import com.hamusuke.numguesser.network.protocol.packet.play.clientbound.ExitGameSuccNotify;
import com.hamusuke.numguesser.network.protocol.packet.room.RoomProtocols;
import com.hamusuke.numguesser.network.protocol.packet.room.clientbound.StartGameNotify;
import com.hamusuke.numguesser.room.Room;
import com.hamusuke.numguesser.room.RoomInfo;
import com.hamusuke.numguesser.server.NumGuesserServer;
import com.hamusuke.numguesser.server.network.ServerPlayer;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class ServerRoom extends Room {
    private final NumGuesserServer server;
    private final List<ServerPlayer> players = Collections.synchronizedList(Lists.newArrayList());
    private final List<ServerPlayer> playerList;
    private final String password;
    private NormalGameMode game;
    private ServerPlayer owner;

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
    public void setGameMode(GameMode gameMode) {
        super.setGameMode(gameMode);
        this.sendPacketToAllInRoom(new GameModeChangeNotify(gameMode));
    }

    public ServerPlayer getOwner() {
        return this.owner;
    }

    public void setOwner(ServerPlayer owner) {
        this.owner = owner;
        this.sendPacketToAllInRoom(new RoomOwnerChangeNotify(this.owner.getId()));
    }

    @Override
    public void tick() {
        super.tick();

        if (this.game != null) {
            this.game.tick();
        }

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
                    this.game = this.gameMode.gameCreator.createGame(this, this.players);
                    this.players.forEach(player -> {
                        player.sendPacket(StartGameNotify.INSTANCE);
                        player.connection.getConnection().setupOutboundProtocol(PlayProtocols.CLIENTBOUND);
                    });

                    this.game.startGame();
                }
            }
        }
    }

    private boolean isPlayerNumValid() {
        return this.players.size() >= this.gameMode.minPlayer && this.players.size() <= this.gameMode.maxPlayer;
    }

    @Override
    public synchronized void join(Player player) {
        var serverPlayer = (ServerPlayer) player;

        if (!this.server.getRoomMap().containsKey(this.id)) {
            serverPlayer.sendPacket(new JoinRoomFailNotify("部屋が見つかりませんでした"));
            return;
        }

        serverPlayer.curRoom = this;
        serverPlayer.sendPacket(new JoinRoomSuccNotify(this.toInfo()));
        serverPlayer.connection.getConnection().setupOutboundProtocol(RoomProtocols.CLIENTBOUND);

        this.sendPacketToAllInRoom(new PlayerJoinNotify(serverPlayer));
        this.players.forEach(player1 -> serverPlayer.sendPacket(new PlayerJoinNotify(player1)));
        this.players.add(serverPlayer);

        if (this.owner != null) {
            serverPlayer.sendPacket(new RoomOwnerChangeNotify(this.owner.getId()));
        }

        serverPlayer.sendPacket(new GameModeChangeNotify(this.gameMode));

        this.sendPacketToAllInRoom(new ChatNotify("%s が部屋に参加しました".formatted(serverPlayer.getDisplayName())));

        this.players.forEach(sp -> this.sendPacketToAllInRoom(new PlayerReadySyncNotify(sp.getId(), sp.isReady())));
    }

    @Override
    public synchronized void leave(Player player) {
        var serverPlayer = (ServerPlayer) player;
        serverPlayer.curRoom = null;
        serverPlayer.setReady(false);
        this.players.remove(serverPlayer);

        if (this.game != null) {
            this.game.leavePlayer(serverPlayer);
        }

        if (this.players.isEmpty()) {
            this.server.removeRoom(this);
            return;
        }

        if (serverPlayer == this.owner) {
            this.setOwner(this.players.get(0));
        }

        this.sendPacketToAllInRoom(new PlayerLeaveNotify(serverPlayer));
        this.sendPacketToAllInRoom(new ChatNotify("%s が部屋から退出しました".formatted(serverPlayer.getDisplayName())));

        if (this.game == null) {
            this.players.forEach(sp -> sp.setReady(false));
        }
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
            this.letPlayerExit(player);
        }
    }

    private void letPlayerExit(ServerPlayer player) {
        player.sendPacket(ExitGameSuccNotify.INSTANCE);
        player.connection.getConnection().setupOutboundProtocol(RoomProtocols.CLIENTBOUND);
    }

    public synchronized void abortGame() {
        var tmp = this.game;
        this.game = null;
        if (tmp != null) {
            tmp.getPlayingPlayers().forEach(this::letPlayerExit);
        }
    }

    public NormalGameMode getGame() {
        return this.game;
    }

    @Nullable
    public ServerPlayer getPlayer(int id) {
        return (ServerPlayer) super.getPlayer(id);
    }
}
