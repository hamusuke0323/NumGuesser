package com.hamusuke.numguesser.network.protocol.packet.play.clientbound;

import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.codec.StreamCodec;
import com.hamusuke.numguesser.network.listener.client.main.ClientPlayPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import com.hamusuke.numguesser.network.protocol.packet.PacketType;
import com.hamusuke.numguesser.network.protocol.packet.play.PlayPacketTypes;

public class PlayerNewDeckNotify implements Packet<ClientPlayPacketListener> {
    public static final PlayerNewDeckNotify INSTANCE = new PlayerNewDeckNotify();
    public static final StreamCodec<IntelligentByteBuf, PlayerNewDeckNotify> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    private PlayerNewDeckNotify() {
    }

    @Override
    public PacketType<PlayerNewDeckNotify> type() {
        return PlayPacketTypes.PLAYER_NEW_DECK;
    }

    @Override
    public void handle(ClientPlayPacketListener listener) {
        listener.handlePlayerNewDeck(this);
    }
}
