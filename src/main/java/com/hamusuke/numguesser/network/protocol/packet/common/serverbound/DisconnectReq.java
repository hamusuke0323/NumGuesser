package com.hamusuke.numguesser.network.protocol.packet.common.serverbound;

import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.codec.StreamCodec;
import com.hamusuke.numguesser.network.listener.server.main.ServerCommonPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import com.hamusuke.numguesser.network.protocol.packet.PacketType;
import com.hamusuke.numguesser.network.protocol.packet.common.CommonPacketTypes;

public class DisconnectReq implements Packet<ServerCommonPacketListener> {
    public static final DisconnectReq INSTANCE = new DisconnectReq();
    public static final StreamCodec<IntelligentByteBuf, DisconnectReq> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    private DisconnectReq() {
    }

    @Override
    public PacketType<DisconnectReq> type() {
        return CommonPacketTypes.DISCONNECT_REQ;
    }

    @Override
    public void handle(ServerCommonPacketListener listener) {
        listener.handleDisconnect(this);
    }
}
