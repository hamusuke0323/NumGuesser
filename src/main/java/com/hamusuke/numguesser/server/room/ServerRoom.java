package com.hamusuke.numguesser.server.room;

import com.hamusuke.numguesser.game.GameMode;
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
import com.hamusuke.numguesser.server.game.GameModeRegistry;
import com.hamusuke.numguesser.server.game.ServerGenericGame;
import com.hamusuke.numguesser.server.network.ServerPlayer;

import java.util.concurrent.atomic.AtomicInteger;

public class ServerRoom extends Room<ServerPlayer> {
    private static final AtomicInteger ROOM_ID_INCREMENTER = new AtomicInteger();
    private final int id = ROOM_ID_INCREMENTER.getAndIncrement();
    private final NumGuesserServer server;
    private final String password;
    private ServerGenericGame game;

    public ServerRoom(NumGuesserServer server, String roomName, String password) {
        super(roomName);
        this.server = server;
        this.password = password;
    }

    public RoomInfo toInfo() {
        return new RoomInfo(this.id, this.roomName, this.getPlayers().size(), this.hasPassword());
    }

    public int getId() {
        return this.id;
    }

    @Override
    public void setGameMode(GameMode gameMode) {
        super.setGameMode(gameMode);
        this.sendPacketToAllInRoom(new GameModeChangeNotify(gameMode));
    }

    @Override
    public void setOwner(ServerPlayer owner) {
        super.setOwner(owner);
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
                    this.game = GameModeRegistry.create(this.gameMode, this, this.players);
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
    public synchronized void join(ServerPlayer player) {
        if (!this.server.getRoomMap().containsKey(this.id)) {
            player.sendPacket(new JoinRoomFailNotify("部屋が見つかりませんでした"));
            return;
        }

        player.curRoom = this;
        player.sendPacket(new JoinRoomSuccNotify(this.toInfo()));
        player.connection.getConnection().setupOutboundProtocol(RoomProtocols.CLIENTBOUND);

        this.sendPacketToAllInRoom(new PlayerJoinNotify(player));
        this.players.forEach(player1 -> player.sendPacket(new PlayerJoinNotify(player1)));
        this.players.add(player);

        if (this.owner != null) {
            player.sendPacket(new RoomOwnerChangeNotify(this.owner.getId()));
        }

        player.sendPacket(new GameModeChangeNotify(this.gameMode));

        this.sendPacketToAllInRoom(new ChatNotify("%s が部屋に参加しました".formatted(player.getDisplayName())));

        this.players.forEach(sp -> this.sendPacketToAllInRoom(new PlayerReadySyncNotify(sp.getId(), sp.isReady())));
    }

    @Override
    public synchronized void leave(ServerPlayer player) {
        player.curRoom = null;
        player.setReady(false);
        this.players.remove(player);

        if (this.game != null) {
            this.game.leavePlayer(player);
        }

        if (this.players.isEmpty()) {
            this.server.removeRoom(this);
            return;
        }

        if (player == this.owner) {
            this.setOwner(this.players.getFirst());
        }

        this.sendPacketToAllInRoom(new PlayerLeaveNotify(player));
        this.sendPacketToAllInRoom(new ChatNotify("%s が部屋から退出しました".formatted(player.getDisplayName())));

        if (this.game == null) {
            this.players.forEach(sp -> sp.setReady(false));
        }
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

    public ServerGenericGame getGame() {
        return this.game;
    }
}
