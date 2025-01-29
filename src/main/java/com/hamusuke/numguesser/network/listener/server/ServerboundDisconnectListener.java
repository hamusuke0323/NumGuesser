package com.hamusuke.numguesser.network.listener.server;

public interface ServerboundDisconnectListener extends ServerboundPacketListener {
    default void handleDisconnectReq() {
    }
}
