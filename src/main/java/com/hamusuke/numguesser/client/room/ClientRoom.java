package com.hamusuke.numguesser.client.room;

import com.google.common.collect.Lists;
import com.hamusuke.numguesser.client.NumGuesser;
import com.hamusuke.numguesser.client.network.player.AbstractClientPlayer;
import com.hamusuke.numguesser.network.Player;
import com.hamusuke.numguesser.room.Room;
import com.hamusuke.numguesser.room.RoomInfo;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class ClientRoom extends Room {
    private final List<AbstractClientPlayer> clientPlayers = Collections.synchronizedList(Lists.newArrayList());
    private final List<AbstractClientPlayer> clientPlayerList;

    public ClientRoom(NumGuesser client, String roomName) {
        super(roomName);
        this.clientPlayerList = Collections.unmodifiableList(this.clientPlayers);
    }

    public static ClientRoom fromRoomInfo(NumGuesser client, RoomInfo info) {
        return new ClientRoom(client, info.roomName());
    }

    @Override
    public synchronized void join(Player player) {
        this.clientPlayers.add((AbstractClientPlayer) player);
    }

    @Override
    public synchronized void leave(Player player) {
        this.clientPlayers.remove((AbstractClientPlayer) player);
    }

    public synchronized void leave(int id) {
        this.clientPlayers.removeIf(p -> p.getId() == id);
    }

    public List<AbstractClientPlayer> getPlayers() {
        return this.clientPlayerList;
    }

    @Nullable
    public AbstractClientPlayer getPlayer(int id) {
        return (AbstractClientPlayer) super.getPlayer(id);
    }
}
