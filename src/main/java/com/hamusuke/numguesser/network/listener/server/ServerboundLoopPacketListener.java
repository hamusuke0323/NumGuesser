package com.hamusuke.numguesser.network.listener.server;

import com.hamusuke.numguesser.network.protocol.packet.loop.serverbound.PongRsp;

public interface ServerboundLoopPacketListener extends ServerboundPacketListener {
    default void handlePong(PongRsp packet) {
    }
}
