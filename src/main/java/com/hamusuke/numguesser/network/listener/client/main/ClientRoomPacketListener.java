package com.hamusuke.numguesser.network.listener.client.main;

import com.hamusuke.numguesser.network.protocol.Protocol;
import com.hamusuke.numguesser.network.protocol.packet.room.clientbound.StartGameNotify;

public interface ClientRoomPacketListener extends ClientCommonPacketListener {
    @Override
    default Protocol protocol() {
        return Protocol.ROOM;
    }

    void handleStartGame(StartGameNotify packet);
}
