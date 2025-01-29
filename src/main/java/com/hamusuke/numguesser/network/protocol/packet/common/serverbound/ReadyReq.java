package com.hamusuke.numguesser.network.protocol.packet.common.serverbound;

import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.codec.StreamCodec;
import com.hamusuke.numguesser.network.listener.server.main.ServerCommonPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import com.hamusuke.numguesser.network.protocol.packet.PacketType;
import com.hamusuke.numguesser.network.protocol.packet.common.CommonPacketTypes;

public class ReadyReq implements Packet<ServerCommonPacketListener> {
    public static final ReadyReq INSTANCE = new ReadyReq();
    public static final StreamCodec<IntelligentByteBuf, ReadyReq> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    private ReadyReq() {
    }

    @Override
    public PacketType<ReadyReq> type() {
        return CommonPacketTypes.READY_REQ;
    }

    @Override
    public void handle(ServerCommonPacketListener listener) {
        listener.handleReady(this);
    }
}
