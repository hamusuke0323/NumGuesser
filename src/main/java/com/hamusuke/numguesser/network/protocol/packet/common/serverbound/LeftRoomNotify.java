package com.hamusuke.numguesser.network.protocol.packet.common.serverbound;

import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.codec.StreamCodec;
import com.hamusuke.numguesser.network.listener.server.main.ServerCommonPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import com.hamusuke.numguesser.network.protocol.packet.PacketType;
import com.hamusuke.numguesser.network.protocol.packet.common.CommonPacketTypes;

public class LeftRoomNotify implements Packet<ServerCommonPacketListener> {
    public static final LeftRoomNotify INSTANCE = new LeftRoomNotify();
    public static final StreamCodec<IntelligentByteBuf, LeftRoomNotify> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    private LeftRoomNotify() {
    }

    @Override
    public PacketType<LeftRoomNotify> type() {
        return CommonPacketTypes.LEFT_ROOM;
    }

    @Override
    public void handle(ServerCommonPacketListener listener) {
        listener.handleLeftRoom(this);
    }

    @Override
    public boolean isTerminal() {
        return true;
    }
}
