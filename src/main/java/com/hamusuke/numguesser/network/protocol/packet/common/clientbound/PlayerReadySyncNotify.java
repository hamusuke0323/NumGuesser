package com.hamusuke.numguesser.network.protocol.packet.common.clientbound;

import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.codec.StreamCodec;
import com.hamusuke.numguesser.network.listener.client.main.ClientCommonPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import com.hamusuke.numguesser.network.protocol.packet.PacketType;
import com.hamusuke.numguesser.network.protocol.packet.common.CommonPacketTypes;

public record PlayerReadySyncNotify(int id, boolean ready) implements Packet<ClientCommonPacketListener> {
    public static final StreamCodec<IntelligentByteBuf, PlayerReadySyncNotify> STREAM_CODEC = Packet.codec(PlayerReadySyncNotify::write, PlayerReadySyncNotify::new);

    private PlayerReadySyncNotify(IntelligentByteBuf buf) {
        this(buf.readVarInt(), buf.readBoolean());
    }

    private void write(IntelligentByteBuf buf) {
        buf.writeVarInt(this.id);
        buf.writeBoolean(this.ready);
    }

    @Override
    public PacketType<PlayerReadySyncNotify> type() {
        return CommonPacketTypes.PLAYER_READY_SYNC;
    }

    @Override
    public void handle(ClientCommonPacketListener listener) {
        listener.handlePlayerReadySync(this);
    }
}
