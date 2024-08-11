package com.hamusuke.numguesser.network.protocol.packet.clientbound.login;

import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.listener.client.login.ClientLoginPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;

public record AliveRsp() implements Packet<ClientLoginPacketListener> {
    public AliveRsp(IntelligentByteBuf byteBuf) {
        this();
    }

    @Override
    public void write(IntelligentByteBuf byteBuf) {
    }

    @Override
    public void handle(ClientLoginPacketListener listener) {
    }
}
