package com.hamusuke.numguesser.network.protocol;

public enum Protocol {
    HANDSHAKING("handshake"),
    LOBBY("lobby"),
    ROOM("room"),
    PLAY("play"),
    LOGIN("login"),
    INFO("info");

    private final String id;

    Protocol(String id) {
        this.id = id;
    }

    public String id() {
        return this.id;
    }
}
