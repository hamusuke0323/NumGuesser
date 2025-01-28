package com.hamusuke.numguesser.network.listener.server;

import com.hamusuke.numguesser.network.listener.PacketListener;
import com.hamusuke.numguesser.network.protocol.PacketDirection;

public interface ServerboundPacketListener extends PacketListener {
    @Override
    default PacketDirection direction() {
        return PacketDirection.SERVERBOUND;
    }
}
