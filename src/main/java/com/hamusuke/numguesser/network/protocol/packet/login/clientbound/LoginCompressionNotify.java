package com.hamusuke.numguesser.network.protocol.packet.login.clientbound;

import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.codec.StreamCodec;
import com.hamusuke.numguesser.network.listener.client.login.ClientLoginPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import com.hamusuke.numguesser.network.protocol.packet.PacketType;
import com.hamusuke.numguesser.network.protocol.packet.login.LoginPacketTypes;

public record LoginCompressionNotify(int threshold) implements Packet<ClientLoginPacketListener> {
    public static final StreamCodec<IntelligentByteBuf, LoginCompressionNotify> STREAM_CODEC = Packet.codec(LoginCompressionNotify::write, LoginCompressionNotify::new);

    private LoginCompressionNotify(IntelligentByteBuf byteBuf) {
        this(byteBuf.readVarInt());
    }

    private void write(IntelligentByteBuf byteBuf) {
        byteBuf.writeVarInt(this.threshold);
    }

    @Override
    public PacketType<LoginCompressionNotify> type() {
        return LoginPacketTypes.LOGIN_COMPRESSION;
    }

    @Override
    public void handle(ClientLoginPacketListener listener) {
        listener.handleCompression(this);
    }
}
