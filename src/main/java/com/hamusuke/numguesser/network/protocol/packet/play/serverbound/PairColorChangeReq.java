package com.hamusuke.numguesser.network.protocol.packet.play.serverbound;

import com.hamusuke.numguesser.game.pair.PlayerPair.PairColor;
import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.codec.StreamCodec;
import com.hamusuke.numguesser.network.listener.server.main.ServerPlayPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import com.hamusuke.numguesser.network.protocol.packet.PacketType;
import com.hamusuke.numguesser.network.protocol.packet.play.PlayPacketTypes;

public record PairColorChangeReq(int id, PairColor color) implements Packet<ServerPlayPacketListener> {
    public static final StreamCodec<IntelligentByteBuf, PairColorChangeReq> STREAM_CODEC = Packet.codec(PairColorChangeReq::write, PairColorChangeReq::new);

    private PairColorChangeReq(IntelligentByteBuf buf) {
        this(buf.readVarInt(), buf.readEnum(PairColor.class));
    }

    private void write(IntelligentByteBuf buf) {
        buf.writeVarInt(this.id);
        buf.writeEnum(this.color);
    }

    @Override
    public PacketType<PairColorChangeReq> type() {
        return PlayPacketTypes.PAIR_COLOR_CHANGE_REQ;
    }

    @Override
    public void handle(ServerPlayPacketListener listener) {
        listener.handlePairColorChange(this);
    }
}
