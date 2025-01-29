package com.hamusuke.numguesser.network.protocol.packet.disconnect.serverbound;

import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.codec.StreamCodec;
import com.hamusuke.numguesser.network.listener.server.ServerboundDisconnectListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import com.hamusuke.numguesser.network.protocol.packet.PacketType;
import com.hamusuke.numguesser.network.protocol.packet.disconnect.DisconnectPacketTypes;

public class DisconnectReq implements Packet<ServerboundDisconnectListener> {
    public static final DisconnectReq INSTANCE = new DisconnectReq();
    public static final StreamCodec<IntelligentByteBuf, DisconnectReq> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    private DisconnectReq() {
    }

    @Override
    public PacketType<DisconnectReq> type() {
        return DisconnectPacketTypes.DISCONNECT_REQ;
    }

    @Override
    public void handle(ServerboundDisconnectListener listener) {
        listener.handleDisconnect(this);
    }
}
