package com.hamusuke.numguesser.network.protocol.packet.play.serverbound;

import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.codec.StreamCodec;
import com.hamusuke.numguesser.network.listener.server.main.ServerPlayPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import com.hamusuke.numguesser.network.protocol.packet.PacketType;
import com.hamusuke.numguesser.network.protocol.packet.play.PlayPacketTypes;

public record TossRsp(int cardId) implements Packet<ServerPlayPacketListener> {
    public static final StreamCodec<IntelligentByteBuf, TossRsp> STREAM_CODEC = Packet.codec(TossRsp::write, TossRsp::new);

    private TossRsp(IntelligentByteBuf buf) {
        this(buf.readVarInt());
    }

    private void write(IntelligentByteBuf buf) {
        buf.writeVarInt(this.cardId);
    }

    @Override
    public PacketType<TossRsp> type() {
        return PlayPacketTypes.TOSS_RSP;
    }

    @Override
    public void handle(ServerPlayPacketListener listener) {
        listener.handleToss(this);
    }
}
