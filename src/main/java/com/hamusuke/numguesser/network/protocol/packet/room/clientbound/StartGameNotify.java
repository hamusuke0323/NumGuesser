package com.hamusuke.numguesser.network.protocol.packet.room.clientbound;

import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.codec.StreamCodec;
import com.hamusuke.numguesser.network.listener.client.main.ClientRoomPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import com.hamusuke.numguesser.network.protocol.packet.PacketType;
import com.hamusuke.numguesser.network.protocol.packet.room.RoomPacketTypes;

public class StartGameNotify implements Packet<ClientRoomPacketListener> {
    public static final StartGameNotify INSTANCE = new StartGameNotify();
    public static final StreamCodec<IntelligentByteBuf, StartGameNotify> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    private StartGameNotify() {
    }

    @Override
    public PacketType<StartGameNotify> type() {
        return RoomPacketTypes.START_GAME;
    }

    @Override
    public void handle(ClientRoomPacketListener listener) {
        listener.handleStartGame(this);
    }

    @Override
    public boolean isTerminal() {
        return true;
    }
}
