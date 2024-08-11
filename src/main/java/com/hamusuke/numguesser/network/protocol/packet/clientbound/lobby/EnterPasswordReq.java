package com.hamusuke.numguesser.network.protocol.packet.clientbound.lobby;

import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.listener.client.lobby.ClientLobbyPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;

public record EnterPasswordReq(int roomId, String msg) implements Packet<ClientLobbyPacketListener> {
    public EnterPasswordReq(IntelligentByteBuf buf) {
        this(buf.readVarInt(), buf.readString());
    }

    @Override
    public void write(IntelligentByteBuf buf) {
        buf.writeVarInt(this.roomId);
        buf.writeString(this.msg);
    }

    @Override
    public void handle(ClientLobbyPacketListener listener) {
        listener.handleEnterPassword(this);
    }
}
