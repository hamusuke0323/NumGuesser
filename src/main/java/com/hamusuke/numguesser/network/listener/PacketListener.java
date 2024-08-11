package com.hamusuke.numguesser.network.listener;

import com.hamusuke.numguesser.network.channel.Connection;

public interface PacketListener {
    void onDisconnected(String msg);

    Connection getConnection();

    default void tick() {
    }

    default boolean shouldCrashOnException() {
        return true;
    }
}
