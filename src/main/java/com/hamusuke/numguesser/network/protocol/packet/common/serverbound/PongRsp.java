package com.hamusuke.numguesser.network.protocol.packet.common.serverbound;

import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.codec.StreamCodec;
import com.hamusuke.numguesser.network.listener.server.main.ServerCommonPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import com.hamusuke.numguesser.network.protocol.packet.PacketType;
import com.hamusuke.numguesser.network.protocol.packet.common.CommonPacketTypes;

public record PongRsp(long serverTime) implements Packet<ServerCommonPacketListener> {
    public static final StreamCodec<IntelligentByteBuf, PongRsp> STREAM_CODEC = Packet.codec(PongRsp::write, PongRsp::new);

    private PongRsp(IntelligentByteBuf byteBuf) {
        this(byteBuf.readVarLong());
    }

    private void write(IntelligentByteBuf byteBuf) {
        byteBuf.writeVarLong(this.serverTime);
    }

    @Override
    public PacketType<PongRsp> type() {
        return CommonPacketTypes.PONG;
    }

    @Override
    public void handle(ServerCommonPacketListener listener) {
        listener.handlePongPacket(this);
    }
}
