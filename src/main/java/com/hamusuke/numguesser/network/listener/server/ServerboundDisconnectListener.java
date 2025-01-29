package com.hamusuke.numguesser.network.listener.server;

import com.hamusuke.numguesser.network.protocol.packet.disconnect.serverbound.DisconnectReq;

public interface ServerboundDisconnectListener extends ServerboundPacketListener {
    default void handleDisconnect(DisconnectReq packet) {
    }
}
