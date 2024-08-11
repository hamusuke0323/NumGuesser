package com.hamusuke.numguesser.network.protocol.packet.clientbound.lobby;

import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.listener.client.lobby.ClientLobbyPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;

public record LobbyPongRsp() implements Packet<ClientLobbyPacketListener> {
    public LobbyPongRsp(IntelligentByteBuf buf) {
        this();
    }

    @Override
    public void write(IntelligentByteBuf buf) {
    }

    @Override
    public void handle(ClientLobbyPacketListener listener) {
        listener.handlePong(this);
    }
}
