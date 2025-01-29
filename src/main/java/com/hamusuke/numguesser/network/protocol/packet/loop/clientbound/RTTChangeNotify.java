package com.hamusuke.numguesser.network.protocol.packet.loop.clientbound;

import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.codec.StreamCodec;
import com.hamusuke.numguesser.network.listener.client.ClientboundLoopPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import com.hamusuke.numguesser.network.protocol.packet.PacketType;
import com.hamusuke.numguesser.network.protocol.packet.loop.LoopPacketTypes;

public record RTTChangeNotify(int id, int rtt) implements Packet<ClientboundLoopPacketListener> {
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
        return LoopPacketTypes.RTT_CHANGE;
    }

    @Override
    public void handle(ClientboundLoopPacketListener listener) {
        listener.handleRTTChange(this);
    }
}
