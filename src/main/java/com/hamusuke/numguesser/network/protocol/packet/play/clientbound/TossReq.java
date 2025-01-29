package com.hamusuke.numguesser.network.protocol.packet.play.clientbound;

import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.codec.StreamCodec;
import com.hamusuke.numguesser.network.listener.client.main.ClientPlayPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import com.hamusuke.numguesser.network.protocol.packet.PacketType;
import com.hamusuke.numguesser.network.protocol.packet.play.PlayPacketTypes;

public class TossReq implements Packet<ClientPlayPacketListener> {
    public static final TossReq INSTANCE = new TossReq();
    public static final StreamCodec<IntelligentByteBuf, TossReq> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    private TossReq() {
    }

    @Override
    public PacketType<TossReq> type() {
        return PlayPacketTypes.TOSS_REQ;
    }

    @Override
    public void handle(ClientPlayPacketListener listener) {
        listener.handleTossReq(this);
    }
}
