package com.hamusuke.numguesser.network;

import com.hamusuke.numguesser.network.protocol.packet.Packet;

import javax.annotation.Nullable;

public interface PacketSendListener {
    static PacketSendListener thenRun(final Runnable runnable) {
        return new PacketSendListener() {
            @Override
            public void onSuccess() {
                runnable.run();
            }

            @Nullable
            @Override
            public Packet<?> onFailure() {
                runnable.run();
                return null;
            }
        };
    }

    default void onSuccess() {
    }

    @Nullable
    default Packet<?> onFailure() {
        return null;
    }
}
