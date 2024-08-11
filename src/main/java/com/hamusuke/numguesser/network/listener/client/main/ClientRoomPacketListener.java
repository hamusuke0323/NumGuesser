package com.hamusuke.numguesser.network.listener.client.main;

import com.hamusuke.numguesser.network.protocol.packet.clientbound.room.StartGameNotify;

public interface ClientRoomPacketListener extends ClientCommonPacketListener {
    void handleStartGame(StartGameNotify packet);
}
