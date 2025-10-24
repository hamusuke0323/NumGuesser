package com.hamusuke.numguesser.network.protocol.packet.play.clientbound;

import com.hamusuke.numguesser.game.phase.PhaseType;
import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.codec.StreamCodec;
import com.hamusuke.numguesser.network.listener.client.main.ClientPlayPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import com.hamusuke.numguesser.network.protocol.packet.PacketType;
import com.hamusuke.numguesser.network.protocol.packet.play.PlayPacketTypes;

public record GamePhaseTransitionNotify(PhaseType phaseType) implements Packet<ClientPlayPacketListener> {
    public static final StreamCodec<IntelligentByteBuf, GamePhaseTransitionNotify> STREAM_CODEC = Packet.codec(GamePhaseTransitionNotify::write, GamePhaseTransitionNotify::new);

    private GamePhaseTransitionNotify(final IntelligentByteBuf buf) {
        this(buf.readEnum(PhaseType.class));
    }

    private void write(final IntelligentByteBuf buf) {
        buf.writeEnum(this.phaseType);
    }

    @Override
    public void handle(ClientPlayPacketListener listener) {
        listener.handleGamePhaseTransition(this);
    }

    @Override
    public PacketType<? extends Packet<ClientPlayPacketListener>> type() {
        return PlayPacketTypes.GAME_PHASE_TRANSITION;
    }
}
