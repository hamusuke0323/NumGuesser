package com.hamusuke.numguesser.network.protocol.packet.play.serverbound;

import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.codec.StreamCodec;
import com.hamusuke.numguesser.network.listener.server.main.ServerPlayPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import com.hamusuke.numguesser.network.protocol.packet.PacketType;
import com.hamusuke.numguesser.network.protocol.packet.play.PlayPacketTypes;

public class PairMakingDoneReq implements Packet<ServerPlayPacketListener> {
    public static final PairMakingDoneReq INSTANCE = new PairMakingDoneReq();
    public static final StreamCodec<IntelligentByteBuf, PairMakingDoneReq> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    private PairMakingDoneReq() {
    }

    @Override
    public PacketType<PairMakingDoneReq> type() {
        return PlayPacketTypes.PAIR_MAKING_DONE;
    }

    @Override
    public void handle(ServerPlayPacketListener listener) {
        listener.handlePairMakingDone(this);
    }
}
