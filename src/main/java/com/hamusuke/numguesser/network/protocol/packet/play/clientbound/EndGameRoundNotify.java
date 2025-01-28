package com.hamusuke.numguesser.network.protocol.packet.play.clientbound;

import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.codec.StreamCodec;
import com.hamusuke.numguesser.network.listener.client.main.ClientPlayPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import com.hamusuke.numguesser.network.protocol.packet.PacketType;
import com.hamusuke.numguesser.network.protocol.packet.play.PlayPacketTypes;

public record EndGameRoundNotify(boolean isFinalRound) implements Packet<ClientPlayPacketListener> {
    public static final StreamCodec<IntelligentByteBuf, EndGameRoundNotify> STREAM_CODEC = Packet.codec(EndGameRoundNotify::write, EndGameRoundNotify::new);

    private EndGameRoundNotify(IntelligentByteBuf buf) {
        this(buf.readBoolean());
    }

    private void write(IntelligentByteBuf buf) {
        buf.writeBoolean(this.isFinalRound);
    }

    @Override
    public PacketType<EndGameRoundNotify> type() {
        return PlayPacketTypes.END_GAME_ROUND;
    }

    @Override
    public void handle(ClientPlayPacketListener listener) {
        listener.handleEndGameRound(this);
    }
}
