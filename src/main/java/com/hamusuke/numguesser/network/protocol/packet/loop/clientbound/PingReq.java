package com.hamusuke.numguesser.network.protocol.packet.loop.clientbound;

import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.codec.StreamCodec;
import com.hamusuke.numguesser.network.listener.client.ClientboundLoopPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import com.hamusuke.numguesser.network.protocol.packet.PacketType;
import com.hamusuke.numguesser.network.protocol.packet.loop.LoopPacketTypes;

public record PingReq(long serverTime) implements Packet<ClientboundLoopPacketListener> {
    public static final StreamCodec<IntelligentByteBuf, PingReq> STREAM_CODEC = Packet.codec(PingReq::write, PingReq::new);

    private PingReq(IntelligentByteBuf byteBuf) {
        this(byteBuf.readVarLong());
    }

    private void write(IntelligentByteBuf byteBuf) {
        byteBuf.writeVarLong(this.serverTime);
    }

    @Override
    public PacketType<PingReq> type() {
        return LoopPacketTypes.PING;
    }

    @Override
    public void handle(ClientboundLoopPacketListener listener) {
        listener.handlePing(this);
    }
}
