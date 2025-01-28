package com.hamusuke.numguesser.network.protocol.packet.play.clientbound;

import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.codec.StreamCodec;
import com.hamusuke.numguesser.network.listener.client.main.ClientPlayPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import com.hamusuke.numguesser.network.protocol.packet.PacketType;
import com.hamusuke.numguesser.network.protocol.packet.play.PlayPacketTypes;

public class ExitGameSuccNotify implements Packet<ClientPlayPacketListener> {
    public static final ExitGameSuccNotify INSTANCE = new ExitGameSuccNotify();
    public static final StreamCodec<IntelligentByteBuf, ExitGameSuccNotify> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    private ExitGameSuccNotify() {
    }

    @Override
    public PacketType<ExitGameSuccNotify> type() {
        return PlayPacketTypes.EXIT_GAME_SUCC;
    }

    @Override
    public void handle(ClientPlayPacketListener listener) {
        listener.handleExitGameSucc(this);
    }

    @Override
    public boolean isTerminal() {
        return true;
    }
}
