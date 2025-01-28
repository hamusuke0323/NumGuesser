package com.hamusuke.numguesser.network.protocol.packet.play.clientbound;

import com.hamusuke.numguesser.game.pair.PlayerPair.PairColor;
import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.codec.StreamCodec;
import com.hamusuke.numguesser.network.listener.client.main.ClientPlayPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import com.hamusuke.numguesser.network.protocol.packet.PacketType;
import com.hamusuke.numguesser.network.protocol.packet.play.PlayPacketTypes;

public record PairColorChangeNotify(int id, PairColor color) implements Packet<ClientPlayPacketListener> {
    public static final StreamCodec<IntelligentByteBuf, PairColorChangeNotify> STREAM_CODEC = Packet.codec(PairColorChangeNotify::write, PairColorChangeNotify::new);

    private PairColorChangeNotify(IntelligentByteBuf buf) {
        this(buf.readVarInt(), buf.readEnum(PairColor.class));
    }

    private void write(IntelligentByteBuf buf) {
        buf.writeVarInt(this.id);
        buf.writeEnum(this.color);
    }

    @Override
    public PacketType<PairColorChangeNotify> type() {
        return PlayPacketTypes.PAIR_COLOR_CHANGE_NOTIFY;
    }

    @Override
    public void handle(ClientPlayPacketListener listener) {
        listener.handlePairColorChange(this);
    }
}
