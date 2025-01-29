package com.hamusuke.numguesser.network.listener.client;

import com.hamusuke.numguesser.network.protocol.packet.loop.clientbound.PingReq;
import com.hamusuke.numguesser.network.protocol.packet.loop.clientbound.RTTChangeNotify;

public interface ClientboundLoopPacketListener extends ClientboundPacketListener {
    default void handlePing(PingReq packet) {
    }

    default void handleRTTChange(RTTChangeNotify packet) {
    }
}
