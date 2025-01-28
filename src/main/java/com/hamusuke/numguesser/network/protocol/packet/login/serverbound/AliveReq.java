package com.hamusuke.numguesser.network.protocol.packet.login.serverbound;

import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.codec.StreamCodec;
import com.hamusuke.numguesser.network.listener.server.login.ServerLoginPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import com.hamusuke.numguesser.network.protocol.packet.PacketType;
import com.hamusuke.numguesser.network.protocol.packet.login.LoginPacketTypes;

public class AliveReq implements Packet<ServerLoginPacketListener> {
    public static final AliveReq INSTANCE = new AliveReq();
    public static final StreamCodec<IntelligentByteBuf, AliveReq> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    private AliveReq() {
    }

    @Override
    public PacketType<AliveReq> type() {
        return LoginPacketTypes.ALIVE_REQ;
    }

    @Override
    public void handle(ServerLoginPacketListener listener) {
        listener.handlePing(this);
    }
}
