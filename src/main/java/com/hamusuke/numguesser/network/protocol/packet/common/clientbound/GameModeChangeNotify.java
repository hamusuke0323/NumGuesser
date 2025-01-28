package com.hamusuke.numguesser.network.protocol.packet.common.clientbound;

import com.hamusuke.numguesser.game.GameMode;
import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.codec.StreamCodec;
import com.hamusuke.numguesser.network.listener.client.main.ClientCommonPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import com.hamusuke.numguesser.network.protocol.packet.PacketType;
import com.hamusuke.numguesser.network.protocol.packet.common.CommonPacketTypes;

public record GameModeChangeNotify(GameMode mode) implements Packet<ClientCommonPacketListener> {
    public static final StreamCodec<IntelligentByteBuf, GameModeChangeNotify> STREAM_CODEC = Packet.codec(GameModeChangeNotify::write, GameModeChangeNotify::new);

    private GameModeChangeNotify(IntelligentByteBuf buf) {
        this(buf.readEnum(GameMode.class));
    }

    private void write(IntelligentByteBuf buf) {
        buf.writeEnum(this.mode);
    }

    @Override
    public PacketType<GameModeChangeNotify> type() {
        return CommonPacketTypes.GAME_MODE_CHANGE;
    }

    @Override
    public void handle(ClientCommonPacketListener listener) {
        listener.handleGameModeChange(this);
    }
}
