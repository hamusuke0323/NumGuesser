package com.hamusuke.numguesser.network.protocol.packet.info.clientbound;

import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.codec.StreamCodec;
import com.hamusuke.numguesser.network.listener.client.info.ClientInfoPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import com.hamusuke.numguesser.network.protocol.packet.PacketType;
import com.hamusuke.numguesser.network.protocol.packet.info.InfoPacketTypes;

public record ServerInfoRsp(int protocolVersion,
                            long clientTimeEcho) implements Packet<ClientInfoPacketListener> {
    public static final StreamCodec<IntelligentByteBuf, ServerInfoRsp> STREAM_CODEC = Packet.codec(ServerInfoRsp::write, ServerInfoRsp::new);

    private ServerInfoRsp(IntelligentByteBuf buf) {
        this(buf.readVarInt(), buf.readVarLong());
    }

    private void write(IntelligentByteBuf buf) {
        buf.writeVarInt(this.protocolVersion);
        buf.writeVarLong(this.clientTimeEcho);
    }

    @Override
    public PacketType<ServerInfoRsp> type() {
        return InfoPacketTypes.SERVER_INFO_RSP;
    }

    @Override
    public void handle(ClientInfoPacketListener listener) {
        listener.handleInfoRsp(this);
    }
}
