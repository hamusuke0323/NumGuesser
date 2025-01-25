package com.hamusuke.numguesser.network.protocol.packet.serverbound.common;

import com.hamusuke.numguesser.game.GameMode;
import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.listener.server.main.ServerCommonPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;

public record GameModeSelectReq(GameMode mode) implements Packet<ServerCommonPacketListener> {
    public GameModeSelectReq(IntelligentByteBuf buf) {
        this(buf.readEnum(GameMode.class));
    }

    @Override
    public void write(IntelligentByteBuf buf) {
        buf.writeEnum(this.mode);
    }

    @Override
    public void handle(ServerCommonPacketListener listener) {
        listener.handleGameModeSelect(this);
    }
}
