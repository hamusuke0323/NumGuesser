package com.hamusuke.numguesser.network.protocol.packet.play.clientbound;

import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.codec.StreamCodec;
import com.hamusuke.numguesser.network.listener.client.main.ClientPlayPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import com.hamusuke.numguesser.network.protocol.packet.PacketType;
import com.hamusuke.numguesser.network.protocol.packet.play.PlayPacketTypes;

public class AttackSuccNotify implements Packet<ClientPlayPacketListener> {
    public static final AttackSuccNotify INSTANCE = new AttackSuccNotify();
    public static final StreamCodec<IntelligentByteBuf, AttackSuccNotify> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    private AttackSuccNotify() {
    }

    @Override
    public PacketType<AttackSuccNotify> type() {
        return PlayPacketTypes.ATTACK_SUCC;
    }

    @Override
    public void handle(ClientPlayPacketListener listener) {
        listener.handleAttackSucc(this);
    }
}
