package com.hamusuke.numguesser.network.protocol.packet.common.clientbound;

import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.codec.StreamCodec;
import com.hamusuke.numguesser.network.listener.client.main.ClientCommonPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import com.hamusuke.numguesser.network.protocol.packet.PacketType;
import com.hamusuke.numguesser.network.protocol.packet.common.CommonPacketTypes;

public record RTTChangeNotify(int id, int rtt) implements Packet<ClientCommonPacketListener> {
    public static final StreamCodec<IntelligentByteBuf, RTTChangeNotify> STREAM_CODEC = Packet.codec(RTTChangeNotify::write, RTTChangeNotify::new);

    private RTTChangeNotify(IntelligentByteBuf byteBuf) {
        this(byteBuf.readVarInt(), byteBuf.readVarInt());
    }

    private void write(IntelligentByteBuf byteBuf) {
        byteBuf.writeVarInt(this.id);
        byteBuf.writeVarInt(this.rtt);
    }

    @Override
    public PacketType<RTTChangeNotify> type() {
        return CommonPacketTypes.RTT_CHANGE;
    }

    @Override
    public void handle(ClientCommonPacketListener listener) {
        listener.handleRTTPacket(this);
    }
}
