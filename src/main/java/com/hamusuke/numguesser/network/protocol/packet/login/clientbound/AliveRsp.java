package com.hamusuke.numguesser.network.protocol.packet.login.clientbound;

import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.codec.StreamCodec;
import com.hamusuke.numguesser.network.listener.client.login.ClientLoginPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import com.hamusuke.numguesser.network.protocol.packet.PacketType;
import com.hamusuke.numguesser.network.protocol.packet.login.LoginPacketTypes;

public class AliveRsp implements Packet<ClientLoginPacketListener> {
    public static final AliveRsp INSTANCE = new AliveRsp();
    public static final StreamCodec<IntelligentByteBuf, AliveRsp> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    private AliveRsp() {
    }

    @Override
    public PacketType<AliveRsp> type() {
        return LoginPacketTypes.ALIVE_RSP;
    }

    @Override
    public void handle(ClientLoginPacketListener listener) {
    }
}
