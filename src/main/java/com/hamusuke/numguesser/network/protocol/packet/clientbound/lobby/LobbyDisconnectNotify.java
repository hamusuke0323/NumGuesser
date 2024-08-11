package com.hamusuke.numguesser.network.protocol.packet.clientbound.lobby;

import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.listener.client.lobby.ClientLobbyPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;

public record LobbyDisconnectNotify(String msg) implements Packet<ClientLobbyPacketListener> {
    public LobbyDisconnectNotify(IntelligentByteBuf buf) {
        this(buf.readString());
    }

    @Override
    public void write(IntelligentByteBuf buf) {
        buf.writeString(msg);
    }

    @Override
    public void handle(ClientLobbyPacketListener listener) {
        listener.handleDisconnectPacket(this);
    }
}
