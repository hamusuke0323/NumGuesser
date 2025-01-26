package com.hamusuke.numguesser.network.protocol.packet.clientbound.play;

import com.hamusuke.numguesser.game.pair.PlayerPair.PairColor;
import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.listener.client.main.ClientPlayPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;

public record PairColorChangeNotify(int id, PairColor color) implements Packet<ClientPlayPacketListener> {
    public PairColorChangeNotify(IntelligentByteBuf buf) {
        this(buf.readVarInt(), buf.readEnum(PairColor.class));
    }

    @Override
    public void write(IntelligentByteBuf buf) {
        buf.writeVarInt(this.id);
        buf.writeEnum(this.color);
    }

    @Override
    public void handle(ClientPlayPacketListener listener) {
        listener.handlePairColorChange(this);
    }
}
