package com.hamusuke.numguesser.room;

import com.hamusuke.numguesser.game.GameMode;
import com.hamusuke.numguesser.network.Player;

import javax.annotation.Nullable;
import java.util.List;

public abstract class Room {
    public static final int MAX_ROOM_NAME_LENGTH = 64;
    public static final int MAX_ROOM_PASSWD_LENGTH = 16;
    protected final String roomName;
    protected GameMode gameMode = GameMode.NORMAL_GAME;

    protected Room(String roomName) {
        this.roomName = roomName;
    }

    public void tick() {
    }

    public String getRoomName() {
        return this.roomName;
    }

    public GameMode getGameMode() {
        return this.gameMode;
    }

    public void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
    }

    public abstract void join(Player player);

    public abstract void leave(Player player);

    public abstract List<? extends Player> getPlayers();

    @Nullable
    public Player getPlayer(int id) {
        return this.getPlayers().stream().filter(player -> player.getId() == id).findFirst().orElse(null);
    }
}
