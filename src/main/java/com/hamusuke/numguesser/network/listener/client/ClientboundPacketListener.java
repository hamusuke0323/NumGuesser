package com.hamusuke.numguesser.network.listener.client;

import com.hamusuke.numguesser.network.listener.PacketListener;
import com.hamusuke.numguesser.network.protocol.PacketDirection;

public interface ClientboundPacketListener extends PacketListener {
    @Override
    default PacketDirection direction() {
        return PacketDirection.CLIENTBOUND;
    }
}
