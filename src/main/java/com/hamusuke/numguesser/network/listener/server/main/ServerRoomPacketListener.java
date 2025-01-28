package com.hamusuke.numguesser.network.listener.server.main;

import com.hamusuke.numguesser.network.protocol.Protocol;
import com.hamusuke.numguesser.network.protocol.packet.room.serverbound.ClientStartedGameNotify;

public interface ServerRoomPacketListener extends ServerCommonPacketListener {
    @Override
    default Protocol protocol() {
        return Protocol.ROOM;
    }

    void handleClientStartedGame(ClientStartedGameNotify packet);
}
