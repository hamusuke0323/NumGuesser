package com.hamusuke.numguesser.network.protocol.packet.common.clientbound;

import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.codec.StreamCodec;
import com.hamusuke.numguesser.network.listener.client.main.ClientCommonPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import com.hamusuke.numguesser.network.protocol.packet.PacketType;
import com.hamusuke.numguesser.network.protocol.packet.common.CommonPacketTypes;

public class ReadyRsp implements Packet<ClientCommonPacketListener> {
    public static final ReadyRsp INSTANCE = new ReadyRsp();
    public static final StreamCodec<IntelligentByteBuf, ReadyRsp> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    private ReadyRsp() {
    }

    @Override
    public PacketType<ReadyRsp> type() {
        return CommonPacketTypes.READY_RSP;
    }

    @Override
    public void handle(ClientCommonPacketListener listener) {
        listener.handleReadyRsp(this);
    }
}
