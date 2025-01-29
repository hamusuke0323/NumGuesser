package com.hamusuke.numguesser.network.protocol.packet.play.serverbound;

import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.codec.StreamCodec;
import com.hamusuke.numguesser.network.listener.server.main.ServerPlayPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import com.hamusuke.numguesser.network.protocol.packet.PacketType;
import com.hamusuke.numguesser.network.protocol.packet.play.PlayPacketTypes;

public class GameExitedNotify implements Packet<ServerPlayPacketListener> {
    public static final GameExitedNotify INSTANCE = new GameExitedNotify();
    public static final StreamCodec<IntelligentByteBuf, GameExitedNotify> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    private GameExitedNotify() {
    }

    @Override
    public PacketType<GameExitedNotify> type() {
        return PlayPacketTypes.GAME_EXITED;
    }

    @Override
    public void handle(ServerPlayPacketListener listener) {
        listener.handleGameExited(this);
    }

    @Override
    public boolean isTerminal() {
        return true;
    }
}
