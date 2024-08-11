package com.hamusuke.numguesser.network.protocol.packet.clientbound.login;

import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.listener.client.login.ClientLoginPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;

public record LoginCompressionNotify(int threshold) implements Packet<ClientLoginPacketListener> {
    public LoginCompressionNotify(IntelligentByteBuf byteBuf) {
        this(byteBuf.readVarInt());
    }

    @Override
    public void write(IntelligentByteBuf byteBuf) {
        byteBuf.writeVarInt(this.threshold);
    }

    @Override
    public void handle(ClientLoginPacketListener listener) {
        listener.handleCompression(this);
    }
}
