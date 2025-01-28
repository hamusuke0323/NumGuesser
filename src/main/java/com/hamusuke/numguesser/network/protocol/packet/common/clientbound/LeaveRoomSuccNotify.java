package com.hamusuke.numguesser.network.protocol.packet.common.clientbound;

import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.codec.StreamCodec;
import com.hamusuke.numguesser.network.listener.client.main.ClientCommonPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import com.hamusuke.numguesser.network.protocol.packet.PacketType;
import com.hamusuke.numguesser.network.protocol.packet.common.CommonPacketTypes;

public class LeaveRoomSuccNotify implements Packet<ClientCommonPacketListener> {
    public static final LeaveRoomSuccNotify INSTANCE = new LeaveRoomSuccNotify();
    public static final StreamCodec<IntelligentByteBuf, LeaveRoomSuccNotify> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    private LeaveRoomSuccNotify() {
    }

    @Override
    public PacketType<LeaveRoomSuccNotify> type() {
        return CommonPacketTypes.LEAVE_ROOM_SUCC;
    }

    @Override
    public void handle(ClientCommonPacketListener listener) {
        listener.handleLeaveRoomSucc(this);
    }

    @Override
    public boolean isTerminal() {
        return true;
    }
}
