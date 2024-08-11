package com.hamusuke.numguesser.network.protocol.packet.clientbound.common;

import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.listener.client.main.ClientCommonPacketListener;
import com.hamusuke.numguesser.network.protocol.Protocol;
import com.hamusuke.numguesser.network.protocol.packet.Packet;

public record LeaveRoomSuccNotify() implements Packet<ClientCommonPacketListener> {
    public LeaveRoomSuccNotify(IntelligentByteBuf buf) {
        this();
    }

    @Override
    public void write(IntelligentByteBuf buf) {
    }

    @Override
    public void handle(ClientCommonPacketListener listener) {
        listener.handleLeaveRoomSucc(this);
    }

    @Override
    public Protocol nextProtocol() {
        return Protocol.LOBBY;
    }
}
