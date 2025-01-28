package com.hamusuke.numguesser.network.protocol.packet.play.clientbound;

import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.codec.StreamCodec;
import com.hamusuke.numguesser.network.listener.client.main.ClientPlayPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import com.hamusuke.numguesser.network.protocol.packet.PacketType;
import com.hamusuke.numguesser.network.protocol.packet.play.PlayPacketTypes;

public class AttackRsp implements Packet<ClientPlayPacketListener> {
    public static final AttackRsp INSTANCE = new AttackRsp();
    public static final StreamCodec<IntelligentByteBuf, AttackRsp> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    private AttackRsp() {
    }

    @Override
    public PacketType<AttackRsp> type() {
        return PlayPacketTypes.ATTACK_RSP;
    }

    @Override
    public void handle(ClientPlayPacketListener listener) {
        listener.handleAttack(this);
    }
}
