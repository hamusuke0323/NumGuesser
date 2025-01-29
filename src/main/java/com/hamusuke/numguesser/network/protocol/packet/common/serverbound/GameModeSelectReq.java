package com.hamusuke.numguesser.network.protocol.packet.common.serverbound;

import com.hamusuke.numguesser.game.GameMode;
import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.codec.StreamCodec;
import com.hamusuke.numguesser.network.listener.server.main.ServerCommonPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import com.hamusuke.numguesser.network.protocol.packet.PacketType;
import com.hamusuke.numguesser.network.protocol.packet.common.CommonPacketTypes;

public record GameModeSelectReq(GameMode mode) implements Packet<ServerCommonPacketListener> {
    public static final StreamCodec<IntelligentByteBuf, GameModeSelectReq> STREAM_CODEC = Packet.codec(GameModeSelectReq::write, GameModeSelectReq::new);

    private GameModeSelectReq(IntelligentByteBuf buf) {
        this(buf.readEnum(GameMode.class));
    }

    private void write(IntelligentByteBuf buf) {
        buf.writeEnum(this.mode);
    }

    @Override
    public PacketType<GameModeSelectReq> type() {
        return CommonPacketTypes.GAME_MODE_SELECT_REQ;
    }

    @Override
    public void handle(ServerCommonPacketListener listener) {
        listener.handleGameModeSelect(this);
    }
}
