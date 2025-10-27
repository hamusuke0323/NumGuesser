package com.hamusuke.numguesser.network.protocol.packet.play.clientbound;

import com.hamusuke.numguesser.game.data.GameDataSyncer;
import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.codec.StreamCodec;
import com.hamusuke.numguesser.network.listener.client.main.ClientPlayPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import com.hamusuke.numguesser.network.protocol.packet.PacketType;
import com.hamusuke.numguesser.network.protocol.packet.play.PlayPacketTypes;

public record GameDataSyncNotify(GameDataSyncer.SerializedData<?> data) implements Packet<ClientPlayPacketListener> {
    public static final StreamCodec<IntelligentByteBuf, GameDataSyncNotify> STREAM_CODEC = StreamCodec.ofMember(GameDataSyncNotify::write, GameDataSyncNotify::new);

    private GameDataSyncNotify(final IntelligentByteBuf buf) {
        this(GameDataSyncer.SerializedData.from(buf));
    }

    private void write(final IntelligentByteBuf buf) {
        this.data.writeTo(buf);
    }

    @Override
    public void handle(ClientPlayPacketListener listener) {
        listener.handleGameDataSync(this);
    }

    @Override
    public PacketType<? extends Packet<ClientPlayPacketListener>> type() {
        return PlayPacketTypes.GAME_DATA_SYNC;
    }
}
