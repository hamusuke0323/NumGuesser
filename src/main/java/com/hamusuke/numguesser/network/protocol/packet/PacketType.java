package com.hamusuke.numguesser.network.protocol.packet;

import com.hamusuke.numguesser.network.protocol.PacketDirection;

public record PacketType<T extends Packet<?>>(PacketDirection direction, String id) {
    @Override
    public String toString() {
        return this.direction.id() + "/" + this.id;
    }
}
