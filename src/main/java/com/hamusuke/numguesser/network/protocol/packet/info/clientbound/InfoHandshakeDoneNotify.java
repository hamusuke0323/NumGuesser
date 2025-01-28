package com.hamusuke.numguesser.network.protocol.packet.info.clientbound;

import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.codec.StreamCodec;
import com.hamusuke.numguesser.network.listener.client.info.ClientInfoPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import com.hamusuke.numguesser.network.protocol.packet.PacketType;
import com.hamusuke.numguesser.network.protocol.packet.info.InfoPacketTypes;

public class InfoHandshakeDoneNotify implements Packet<ClientInfoPacketListener> {
    public static final InfoHandshakeDoneNotify INSTANCE = new InfoHandshakeDoneNotify();
    public static final StreamCodec<IntelligentByteBuf, InfoHandshakeDoneNotify> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    private InfoHandshakeDoneNotify() {
    }

    @Override
    public PacketType<InfoHandshakeDoneNotify> type() {
        return InfoPacketTypes.INFO_HANDSHAKE_DONE;
    }

    @Override
    public void handle(ClientInfoPacketListener listener) {
        listener.handleHandshakeDone(this);
    }
}
