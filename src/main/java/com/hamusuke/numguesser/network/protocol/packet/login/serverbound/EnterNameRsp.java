package com.hamusuke.numguesser.network.protocol.packet.login.serverbound;

import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.codec.StreamCodec;
import com.hamusuke.numguesser.network.listener.server.login.ServerLoginPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import com.hamusuke.numguesser.network.protocol.packet.PacketType;
import com.hamusuke.numguesser.network.protocol.packet.login.LoginPacketTypes;

public record EnterNameRsp(String name) implements Packet<ServerLoginPacketListener> {
    public static final StreamCodec<IntelligentByteBuf, EnterNameRsp> STREAM_CODEC = Packet.codec(EnterNameRsp::write, EnterNameRsp::new);
    public static final int MAX_NAME_LENGTH = 16;

    private EnterNameRsp(IntelligentByteBuf byteBuf) {
        this(byteBuf.readString(MAX_NAME_LENGTH));
    }

    private void write(IntelligentByteBuf byteBuf) {
        byteBuf.writeString(this.name, MAX_NAME_LENGTH);
    }

    @Override
    public PacketType<EnterNameRsp> type() {
        return LoginPacketTypes.ENTER_NAME_RSP;
    }

    @Override
    public void handle(ServerLoginPacketListener listener) {
        listener.handleEnterName(this);
    }
}
