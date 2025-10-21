package com.hamusuke.numguesser.room;

import com.google.common.collect.Lists;
import com.hamusuke.numguesser.game.GameMode;
import com.hamusuke.numguesser.network.Player;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public abstract class Room<P extends Player> {
    public static final int MAX_ROOM_NAME_LENGTH = 64;
    public static final int MAX_ROOM_PASSWD_LENGTH = 16;
    protected final String roomName;
    protected final List<P> players = Collections.synchronizedList(Lists.newArrayList());
    private final List<P> playerList;
    protected P owner;
    protected GameMode gameMode = GameMode.NORMAL_GAME;

    protected Room(String roomName) {
        this.roomName = roomName;
        this.playerList = Collections.unmodifiableList(this.players);
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

    public P getOwner() {
        return this.owner;
    }

    public void setOwner(P owner) {
        this.owner = owner;
    }

    public synchronized void join(P player) {
        this.players.add(player);
    }

    public synchronized void leave(P player) {
        this.players.remove(player);
    }

    public List<P> getPlayers() {
        return this.playerList;
    }

    @Nullable
    public P getPlayer(int id) {
        return this.getPlayers().stream().filter(player -> player.getId() == id).findFirst().orElse(null);
    }
}
