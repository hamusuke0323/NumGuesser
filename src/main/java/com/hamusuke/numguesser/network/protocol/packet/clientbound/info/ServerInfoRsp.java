package com.hamusuke.numguesser.network.protocol.packet.clientbound.info;

import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.listener.client.info.ClientInfoPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;

public record ServerInfoRsp(int protocolVersion,
                            long clientTimeEcho) implements Packet<ClientInfoPacketListener> {
    public ServerInfoRsp(IntelligentByteBuf buf) {
        this(buf.readVarInt(), buf.readLong());
    }

    @Override
    public void write(IntelligentByteBuf buf) {
        buf.writeVarInt(this.protocolVersion);
        buf.writeLong(this.clientTimeEcho);
    }

    @Override
    public void handle(ClientInfoPacketListener listener) {
        listener.handleInfoRsp(this);
    }
}
