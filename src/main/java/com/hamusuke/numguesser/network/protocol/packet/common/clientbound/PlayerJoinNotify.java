package com.hamusuke.numguesser.network.protocol.packet.common.clientbound;

import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.codec.StreamCodec;
import com.hamusuke.numguesser.network.listener.client.main.ClientCommonPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import com.hamusuke.numguesser.network.protocol.packet.PacketType;
import com.hamusuke.numguesser.network.protocol.packet.common.CommonPacketTypes;
import com.hamusuke.numguesser.server.network.ServerPlayer;

public record PlayerJoinNotify(int id, String name) implements Packet<ClientCommonPacketListener> {
    public static final StreamCodec<IntelligentByteBuf, PlayerJoinNotify> STREAM_CODEC = Packet.codec(PlayerJoinNotify::write, PlayerJoinNotify::new);

    public PlayerJoinNotify(ServerPlayer player) {
        this(player.getId(), player.getName());
    }

    private PlayerJoinNotify(IntelligentByteBuf byteBuf) {
        this(byteBuf.readVarInt(), byteBuf.readString());
    }

    private void write(IntelligentByteBuf byteBuf) {
        byteBuf.writeVarInt(this.id);
        byteBuf.writeString(this.name);
    }

    @Override
    public PacketType<PlayerJoinNotify> type() {
        return CommonPacketTypes.PLAYER_JOIN;
    }

    @Override
    public void handle(ClientCommonPacketListener listener) {
        listener.handleJoinPacket(this);
    }
}
