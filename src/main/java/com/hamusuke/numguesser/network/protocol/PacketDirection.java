package com.hamusuke.numguesser.network.protocol;

public enum PacketDirection {
    SERVERBOUND("serverbound"),
    CLIENTBOUND("clientbound");

    private final String id;

    PacketDirection(String id) {
        this.id = id;
    }

    public PacketDirection getOpposite() {
        return this == CLIENTBOUND ? SERVERBOUND : CLIENTBOUND;
    }

    public String id() {
        return this.id;
    }
}
