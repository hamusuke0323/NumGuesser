package com.hamusuke.numguesser.network.protocol.packet.play.clientbound;

import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.codec.StreamCodec;
import com.hamusuke.numguesser.network.listener.client.main.ClientPlayPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import com.hamusuke.numguesser.network.protocol.packet.PacketType;
import com.hamusuke.numguesser.network.protocol.packet.play.PlayPacketTypes;

public class StartGameRoundNotify implements Packet<ClientPlayPacketListener> {
    public static final StartGameRoundNotify INSTANCE = new StartGameRoundNotify();
    public static final StreamCodec<IntelligentByteBuf, StartGameRoundNotify> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    private StartGameRoundNotify() {
    }

    @Override
    public PacketType<StartGameRoundNotify> type() {
        return PlayPacketTypes.START_GAME_ROUND;
    }

    @Override
    public void handle(ClientPlayPacketListener listener) {
        listener.handleStartGameRound(this);
    }
}
