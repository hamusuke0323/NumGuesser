package com.hamusuke.numguesser.network.listener.server;

import com.hamusuke.numguesser.network.listener.PacketListener;

public interface ServerPacketListener extends PacketListener {
    @Override
    default boolean shouldCrashOnException() {
        return false;
    }
}
