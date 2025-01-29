package com.hamusuke.numguesser.network.protocol.packet.info.serverbound;

import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.codec.StreamCodec;
import com.hamusuke.numguesser.network.listener.server.info.ServerInfoPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import com.hamusuke.numguesser.network.protocol.packet.PacketType;
import com.hamusuke.numguesser.network.protocol.packet.info.InfoPacketTypes;

public record ServerInfoReq(long clientTime) implements Packet<ServerInfoPacketListener> {
    public static final StreamCodec<IntelligentByteBuf, ServerInfoReq> STREAM_CODEC = Packet.codec(ServerInfoReq::write, ServerInfoReq::new);

    private ServerInfoReq(IntelligentByteBuf buf) {
        this(buf.readVarLong());
    }

    private void write(IntelligentByteBuf buf) {
        buf.writeVarLong(this.clientTime);
    }

    @Override
    public PacketType<ServerInfoReq> type() {
        return InfoPacketTypes.SERVER_INFO_REQ;
    }

    @Override
    public void handle(ServerInfoPacketListener listener) {
        listener.handleInfoReq(this);
    }
}
