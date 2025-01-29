package com.hamusuke.numguesser.network.protocol.packet.loop.serverbound;

import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.codec.StreamCodec;
import com.hamusuke.numguesser.network.listener.server.ServerboundLoopPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import com.hamusuke.numguesser.network.protocol.packet.PacketType;
import com.hamusuke.numguesser.network.protocol.packet.loop.LoopPacketTypes;

public record PongRsp(long serverTime) implements Packet<ServerboundLoopPacketListener> {
    public static final StreamCodec<IntelligentByteBuf, PongRsp> STREAM_CODEC = Packet.codec(PongRsp::write, PongRsp::new);

    private PongRsp(IntelligentByteBuf byteBuf) {
        this(byteBuf.readVarLong());
    }

    private void write(IntelligentByteBuf byteBuf) {
        byteBuf.writeVarLong(this.serverTime);
    }

    @Override
    public PacketType<PongRsp> type() {
        return LoopPacketTypes.PONG;
    }

    @Override
    public void handle(ServerboundLoopPacketListener listener) {
        listener.handlePong(this);
    }
}
