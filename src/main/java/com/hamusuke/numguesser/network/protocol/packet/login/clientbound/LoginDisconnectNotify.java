package com.hamusuke.numguesser.network.protocol.packet.login.clientbound;

import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.codec.StreamCodec;
import com.hamusuke.numguesser.network.listener.client.login.ClientLoginPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import com.hamusuke.numguesser.network.protocol.packet.PacketType;
import com.hamusuke.numguesser.network.protocol.packet.login.LoginPacketTypes;

public record LoginDisconnectNotify(String msg) implements Packet<ClientLoginPacketListener> {
    public static final StreamCodec<IntelligentByteBuf, LoginDisconnectNotify> STREAM_CODEC = Packet.codec(LoginDisconnectNotify::write, LoginDisconnectNotify::new);

    private LoginDisconnectNotify(IntelligentByteBuf byteBuf) {
        this(byteBuf.readString());
    }

    private void write(IntelligentByteBuf byteBuf) {
        byteBuf.writeString(this.msg);
    }

    @Override
    public PacketType<LoginDisconnectNotify> type() {
        return LoginPacketTypes.LOGIN_DISCONNECT;
    }

    @Override
    public void handle(ClientLoginPacketListener listener) {
        listener.handleDisconnect(this);
    }
}
