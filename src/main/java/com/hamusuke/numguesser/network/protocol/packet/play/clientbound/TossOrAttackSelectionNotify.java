package com.hamusuke.numguesser.network.protocol.packet.play.clientbound;

import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.codec.StreamCodec;
import com.hamusuke.numguesser.network.listener.client.main.ClientPlayPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import com.hamusuke.numguesser.network.protocol.packet.PacketType;
import com.hamusuke.numguesser.network.protocol.packet.play.PlayPacketTypes;

public class TossOrAttackSelectionNotify implements Packet<ClientPlayPacketListener> {
    public static final TossOrAttackSelectionNotify INSTANCE = new TossOrAttackSelectionNotify();
    public static final StreamCodec<IntelligentByteBuf, TossOrAttackSelectionNotify> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    private TossOrAttackSelectionNotify() {
    }

    @Override
    public PacketType<TossOrAttackSelectionNotify> type() {
        return PlayPacketTypes.TOSS_OR_ATTACK_SELECTION;
    }

    @Override
    public void handle(ClientPlayPacketListener listener) {
        listener.handleTossOrAttackSelection(this);
    }
}
