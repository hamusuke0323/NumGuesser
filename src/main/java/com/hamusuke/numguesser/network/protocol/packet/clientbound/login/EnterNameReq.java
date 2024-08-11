package com.hamusuke.numguesser.network.protocol.packet.clientbound.login;

import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.listener.client.login.ClientLoginPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;

public record EnterNameReq(String msg) implements Packet<ClientLoginPacketListener> {
    public EnterNameReq() {
        this("");
    }

    public EnterNameReq(IntelligentByteBuf byteBuf) {
        this(byteBuf.readString());
    }

    @Override
    public void write(IntelligentByteBuf byteBuf) {
        byteBuf.writeString(this.msg);
    }

    @Override
    public void handle(ClientLoginPacketListener listener) {
        listener.handleEnterName(this);
    }
}
