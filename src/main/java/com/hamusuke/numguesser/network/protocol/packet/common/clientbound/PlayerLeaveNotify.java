package com.hamusuke.numguesser.network.protocol.packet.common.clientbound;

import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.codec.StreamCodec;
import com.hamusuke.numguesser.network.listener.client.main.ClientCommonPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import com.hamusuke.numguesser.network.protocol.packet.PacketType;
import com.hamusuke.numguesser.network.protocol.packet.common.CommonPacketTypes;
import com.hamusuke.numguesser.server.network.ServerPlayer;

public record PlayerLeaveNotify(int id) implements Packet<ClientCommonPacketListener> {
    public static final StreamCodec<IntelligentByteBuf, PlayerLeaveNotify> STREAM_CODEC = Packet.codec(PlayerLeaveNotify::write, PlayerLeaveNotify::new);

    private PlayerLeaveNotify(IntelligentByteBuf byteBuf) {
        this(byteBuf.readVarInt());
    }

    public PlayerLeaveNotify(ServerPlayer player) {
        this(player.getId());
    }

    private void write(IntelligentByteBuf byteBuf) {
        byteBuf.writeVarInt(this.id);
    }

    @Override
    public PacketType<PlayerLeaveNotify> type() {
        return CommonPacketTypes.PLAYER_LEAVE;
    }

    @Override
    public void handle(ClientCommonPacketListener listener) {
        listener.handleLeavePacket(this);
    }
}
