package com.hamusuke.numguesser.network.protocol.packet.common.clientbound;

import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.codec.StreamCodec;
import com.hamusuke.numguesser.network.listener.client.main.ClientCommonPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import com.hamusuke.numguesser.network.protocol.packet.PacketType;
import com.hamusuke.numguesser.network.protocol.packet.common.CommonPacketTypes;

public record DisconnectNotify(String msg) implements Packet<ClientCommonPacketListener> {
    public static final StreamCodec<IntelligentByteBuf, DisconnectNotify> STREAM_CODEC = Packet.codec(DisconnectNotify::write, DisconnectNotify::new);

    private DisconnectNotify(IntelligentByteBuf byteBuf) {
        this(byteBuf.readString());
    }

    private void write(IntelligentByteBuf byteBuf) {
        byteBuf.writeString(this.msg);
    }

    @Override
    public PacketType<DisconnectNotify> type() {
        return CommonPacketTypes.DISCONNECT_NOTIFY;
    }

    @Override
    public void handle(ClientCommonPacketListener listener) {
        listener.handleDisconnectPacket(this);
    }
}
