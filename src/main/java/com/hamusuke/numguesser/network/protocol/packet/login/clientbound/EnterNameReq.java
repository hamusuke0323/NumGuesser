package com.hamusuke.numguesser.network.protocol.packet.login.clientbound;

import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.codec.StreamCodec;
import com.hamusuke.numguesser.network.listener.client.login.ClientLoginPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import com.hamusuke.numguesser.network.protocol.packet.PacketType;
import com.hamusuke.numguesser.network.protocol.packet.login.LoginPacketTypes;

public record EnterNameReq(String msg) implements Packet<ClientLoginPacketListener> {
    public static final StreamCodec<IntelligentByteBuf, EnterNameReq> STREAM_CODEC = Packet.codec(EnterNameReq::write, EnterNameReq::new);

    public EnterNameReq() {
        this("");
    }

    private EnterNameReq(IntelligentByteBuf byteBuf) {
        this(byteBuf.readString());
    }

    private void write(IntelligentByteBuf byteBuf) {
        byteBuf.writeString(this.msg);
    }

    @Override
    public PacketType<EnterNameReq> type() {
        return LoginPacketTypes.ENTER_NAME_REQ;
    }

    @Override
    public void handle(ClientLoginPacketListener listener) {
        listener.handleEnterName(this);
    }
}
