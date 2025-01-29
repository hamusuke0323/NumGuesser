package com.hamusuke.numguesser.network.protocol.packet.handshake.serverbound;

public enum ClientIntent {
    INFO,
    LOGIN;

    private static final int INFO_ID = 1;
    private static final int LOGIN_ID = 2;

    public static ClientIntent byId(int id) {
        return switch (id) {
            case INFO_ID -> INFO;
            case LOGIN_ID -> LOGIN;
            default -> throw new IllegalArgumentException("Unknown connection intention: " + id);
        };
    }

    public int id() {
        return switch (this.ordinal()) {
            case 0 -> INFO_ID;
            case 1 -> LOGIN_ID;
            default -> throw new IllegalStateException();
        };
    }
}
