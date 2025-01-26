package com.hamusuke.numguesser.network.protocol.packet.serverbound.play;

import com.hamusuke.numguesser.game.pair.PlayerPair.PairColor;
import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.listener.server.main.ServerPlayPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;

public record PairColorChangeReq(int id, PairColor color) implements Packet<ServerPlayPacketListener> {
    public PairColorChangeReq(IntelligentByteBuf buf) {
        this(buf.readVarInt(), buf.readEnum(PairColor.class));
    }

    @Override
    public void write(IntelligentByteBuf buf) {
        buf.writeVarInt(this.id);
        buf.writeEnum(this.color);
    }

    @Override
    public void handle(ServerPlayPacketListener listener) {
        listener.handlePairColorChange(this);
    }
}
