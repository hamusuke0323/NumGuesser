package com.hamusuke.numguesser.network.protocol.packet;

import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.listener.PacketListener;
import com.hamusuke.numguesser.network.protocol.Protocol;

import javax.annotation.Nullable;

public interface Packet<T extends PacketListener> {
    void write(IntelligentByteBuf buf);

    void handle(T listener);

    @Nullable
    default Protocol nextProtocol() {
        return null;
    }
}
