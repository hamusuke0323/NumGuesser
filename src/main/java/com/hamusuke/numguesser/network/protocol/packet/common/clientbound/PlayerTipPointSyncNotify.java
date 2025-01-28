package com.hamusuke.numguesser.network.protocol.packet.common.clientbound;

import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.codec.StreamCodec;
import com.hamusuke.numguesser.network.listener.client.main.ClientCommonPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import com.hamusuke.numguesser.network.protocol.packet.PacketType;
import com.hamusuke.numguesser.network.protocol.packet.common.CommonPacketTypes;

public record PlayerTipPointSyncNotify(int id, int tipPoint) implements Packet<ClientCommonPacketListener> {
    public static final StreamCodec<IntelligentByteBuf, PlayerTipPointSyncNotify> STREAM_CODEC = Packet.codec(PlayerTipPointSyncNotify::write, PlayerTipPointSyncNotify::new);

    private PlayerTipPointSyncNotify(IntelligentByteBuf buf) {
        this(buf.readVarInt(), buf.readVarInt());
    }

    private void write(IntelligentByteBuf buf) {
        buf.writeVarInt(this.id);
        buf.writeVarInt(this.tipPoint);
    }

    @Override
    public PacketType<PlayerTipPointSyncNotify> type() {
        return CommonPacketTypes.PLAYER_TIP_POINT_SYNC;
    }

    @Override
    public void handle(ClientCommonPacketListener listener) {
        listener.handlePlayerTipPointSync(this);
    }
}
