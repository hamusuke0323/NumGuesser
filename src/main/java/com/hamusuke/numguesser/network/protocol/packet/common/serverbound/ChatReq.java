package com.hamusuke.numguesser.network.protocol.packet.common.serverbound;

import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.codec.StreamCodec;
import com.hamusuke.numguesser.network.listener.server.main.ServerCommonPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import com.hamusuke.numguesser.network.protocol.packet.PacketType;
import com.hamusuke.numguesser.network.protocol.packet.common.CommonPacketTypes;

public record ChatReq(String msg) implements Packet<ServerCommonPacketListener> {
    public static final StreamCodec<IntelligentByteBuf, ChatReq> STREAM_CODEC = Packet.codec(ChatReq::write, ChatReq::new);

    private ChatReq(IntelligentByteBuf byteBuf) {
        this(byteBuf.readString());
    }

    private void write(IntelligentByteBuf byteBuf) {
        byteBuf.writeString(this.msg);
    }

    @Override
    public PacketType<ChatReq> type() {
        return CommonPacketTypes.CHAT_REQ;
    }

    @Override
    public void handle(ServerCommonPacketListener listener) {
        listener.handleChatPacket(this);
    }
}
