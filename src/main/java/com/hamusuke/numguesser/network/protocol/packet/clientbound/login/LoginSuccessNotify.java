package com.hamusuke.numguesser.network.protocol.packet.clientbound.login;

import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.listener.client.login.ClientLoginPacketListener;
import com.hamusuke.numguesser.network.protocol.Protocol;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import com.hamusuke.numguesser.server.network.ServerPlayer;

public record LoginSuccessNotify(int id, String name) implements Packet<ClientLoginPacketListener> {
    public LoginSuccessNotify(ServerPlayer serverPlayer) {
        this(serverPlayer.getId(), serverPlayer.getName());
    }

    public LoginSuccessNotify(IntelligentByteBuf byteBuf) {
        this(byteBuf.readVarInt(), byteBuf.readString());
    }

    @Override
    public void write(IntelligentByteBuf byteBuf) {
        byteBuf.writeVarInt(this.id);
        byteBuf.writeString(this.name);
    }

    @Override
    public void handle(ClientLoginPacketListener listener) {
        listener.handleSuccess(this);
    }

    @Override
    public Protocol nextProtocol() {
        return Protocol.LOBBY;
    }
}
