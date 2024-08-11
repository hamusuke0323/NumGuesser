package com.hamusuke.numguesser.network.protocol.packet.clientbound.room;

import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.listener.client.main.ClientRoomPacketListener;
import com.hamusuke.numguesser.network.protocol.Protocol;
import com.hamusuke.numguesser.network.protocol.packet.Packet;

public record StartGameNotify() implements Packet<ClientRoomPacketListener> {
    public StartGameNotify(IntelligentByteBuf buf) {
        this();
    }

    @Override
    public void write(IntelligentByteBuf buf) {
    }

    @Override
    public void handle(ClientRoomPacketListener listener) {
        listener.handleStartGame(this);
    }

    @Override
    public Protocol nextProtocol() {
        return Protocol.PLAY;
    }
}
