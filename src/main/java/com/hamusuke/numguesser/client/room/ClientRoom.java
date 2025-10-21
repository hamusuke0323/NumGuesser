package com.hamusuke.numguesser.client.room;

import com.hamusuke.numguesser.client.NumGuesser;
import com.hamusuke.numguesser.client.network.player.AbstractClientPlayer;
import com.hamusuke.numguesser.room.Room;
import com.hamusuke.numguesser.room.RoomInfo;

public class ClientRoom extends Room<AbstractClientPlayer> {
    private final NumGuesser client;

    public ClientRoom(NumGuesser client, String roomName) {
        super(roomName);
        this.client = client;
    }

    public static ClientRoom fromRoomInfo(NumGuesser client, RoomInfo info) {
        return new ClientRoom(client, info.roomName());
    }

    public boolean amIOwner() {
        return this.owner == this.client.clientPlayer;
    }

    public synchronized void leave(int id) {
        this.players.removeIf(p -> p.getId() == id);
    }
}
