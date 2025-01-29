package com.hamusuke.numguesser.network.protocol.packet.common.serverbound;

import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.codec.StreamCodec;
import com.hamusuke.numguesser.network.listener.server.main.ServerCommonPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import com.hamusuke.numguesser.network.protocol.packet.PacketType;
import com.hamusuke.numguesser.network.protocol.packet.common.CommonPacketTypes;

public class LeaveRoomReq implements Packet<ServerCommonPacketListener> {
    public static final LeaveRoomReq INSTANCE = new LeaveRoomReq();
    public static final StreamCodec<IntelligentByteBuf, LeaveRoomReq> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    private LeaveRoomReq() {
    }

    @Override
    public PacketType<LeaveRoomReq> type() {
        return CommonPacketTypes.LEAVE_ROOM_REQ;
    }

    @Override
    public void handle(ServerCommonPacketListener listener) {
        listener.handleLeaveRoom(this);
    }
}
