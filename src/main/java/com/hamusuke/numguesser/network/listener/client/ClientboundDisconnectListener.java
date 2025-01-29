package com.hamusuke.numguesser.network.listener.client;

import com.hamusuke.numguesser.network.protocol.packet.disconnect.clientbound.DisconnectNotify;

public interface ClientboundDisconnectListener extends ClientboundPacketListener {
    default void handleDisconnect(DisconnectNotify packet) {
    }
}
