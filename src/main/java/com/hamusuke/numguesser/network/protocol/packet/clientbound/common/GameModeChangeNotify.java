package com.hamusuke.numguesser.network.protocol.packet.clientbound.common;

import com.hamusuke.numguesser.game.GameMode;
import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.listener.client.main.ClientCommonPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;

public record GameModeChangeNotify(GameMode mode) implements Packet<ClientCommonPacketListener> {
    public GameModeChangeNotify(IntelligentByteBuf buf) {
        this(buf.readEnum(GameMode.class));
    }

    @Override
    public void write(IntelligentByteBuf buf) {
        buf.writeEnum(this.mode);
    }

    @Override
    public void handle(ClientCommonPacketListener listener) {
        listener.handleGameModeChange(this);
    }
}
