package com.hamusuke.numguesser.network.protocol.packet.room.serverbound;

import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.codec.StreamCodec;
import com.hamusuke.numguesser.network.listener.server.main.ServerRoomPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import com.hamusuke.numguesser.network.protocol.packet.PacketType;
import com.hamusuke.numguesser.network.protocol.packet.room.RoomPacketTypes;

public class ClientStartedGameNotify implements Packet<ServerRoomPacketListener> {
    public static final ClientStartedGameNotify INSTANCE = new ClientStartedGameNotify();
    public static final StreamCodec<IntelligentByteBuf, ClientStartedGameNotify> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    private ClientStartedGameNotify() {
    }

    @Override
    public PacketType<ClientStartedGameNotify> type() {
        return RoomPacketTypes.CLIENT_STARTED_GAME;
    }

    @Override
    public void handle(ServerRoomPacketListener listener) {
        listener.handleClientStartedGame(this);
    }

    @Override
    public boolean isTerminal() {
        return true;
    }
}
