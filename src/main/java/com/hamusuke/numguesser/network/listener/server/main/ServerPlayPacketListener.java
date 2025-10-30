package com.hamusuke.numguesser.network.listener.server.main;

import com.hamusuke.numguesser.network.protocol.Protocol;
import com.hamusuke.numguesser.network.protocol.packet.play.serverbound.ClientActionReq;
import com.hamusuke.numguesser.network.protocol.packet.play.serverbound.ClientCommandReq;
import com.hamusuke.numguesser.network.protocol.packet.play.serverbound.GameExitedNotify;

public interface ServerPlayPacketListener extends ServerCommonPacketListener {
    @Override
    default Protocol protocol() {
        return Protocol.PLAY;
    }

    void handleClientAction(ClientActionReq packet);

    void handleClientCommand(ClientCommandReq packet);

    void handleGameExited(GameExitedNotify packet);
}
