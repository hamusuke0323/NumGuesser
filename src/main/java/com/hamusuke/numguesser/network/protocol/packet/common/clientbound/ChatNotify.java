package com.hamusuke.numguesser.network.protocol.packet.common.clientbound;

import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.codec.StreamCodec;
import com.hamusuke.numguesser.network.listener.client.main.ClientCommonPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import com.hamusuke.numguesser.network.protocol.packet.PacketType;
import com.hamusuke.numguesser.network.protocol.packet.common.CommonPacketTypes;

public record ChatNotify(String msg) implements Packet<ClientCommonPacketListener> {
    public static final StreamCodec<IntelligentByteBuf, ChatNotify> STREAM_CODEC = Packet.codec(ChatNotify::write, ChatNotify::new);

    private ChatNotify(IntelligentByteBuf byteBuf) {
        this(byteBuf.readString());
    }

    private void write(IntelligentByteBuf byteBuf) {
        byteBuf.writeString(this.msg);
    }

    @Override
    public PacketType<ChatNotify> type() {
        return CommonPacketTypes.CHAT_NOTIFY;
    }

    @Override
    public void handle(ClientCommonPacketListener listener) {
        listener.handleChatPacket(this);
    }
}
