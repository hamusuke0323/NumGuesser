package com.hamusuke.numguesser.network.protocol.packet.login.serverbound;

import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.codec.StreamCodec;
import com.hamusuke.numguesser.network.listener.server.login.ServerLoginPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import com.hamusuke.numguesser.network.protocol.packet.PacketType;
import com.hamusuke.numguesser.network.protocol.packet.login.LoginPacketTypes;

public class KeyExchangeReq implements Packet<ServerLoginPacketListener> {
    public static final KeyExchangeReq INSTANCE = new KeyExchangeReq();
    public static final StreamCodec<IntelligentByteBuf, KeyExchangeReq> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    private KeyExchangeReq() {
    }

    @Override
    public PacketType<KeyExchangeReq> type() {
        return LoginPacketTypes.KEY_EXCHANGE_REQ;
    }

    @Override
    public void handle(ServerLoginPacketListener listener) {
        listener.handleKeyEx(this);
    }
}
