package com.hamusuke.numguesser.network.listener;

public interface TickablePacketListener extends PacketListener {
    void tick();
}
