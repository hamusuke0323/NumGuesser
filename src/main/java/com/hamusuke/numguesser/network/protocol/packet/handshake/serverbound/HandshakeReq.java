package com.hamusuke.numguesser.network.protocol.packet.handshake.serverbound;

import com.hamusuke.numguesser.Constants;
import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.codec.StreamCodec;
import com.hamusuke.numguesser.network.listener.server.handshake.ServerHandshakePacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import com.hamusuke.numguesser.network.protocol.packet.PacketType;
import com.hamusuke.numguesser.network.protocol.packet.handshake.HandshakePacketTypes;

public record HandshakeReq(int protocolVersion,
                           ClientIntent intention) implements Packet<ServerHandshakePacketListener> {
    public static final StreamCodec<IntelligentByteBuf, HandshakeReq> STREAM_CODEC = Packet.codec(HandshakeReq::write, HandshakeReq::new);

    public HandshakeReq(ClientIntent intention) {
        this(Constants.PROTOCOL_VERSION, intention);
    }

    private HandshakeReq(IntelligentByteBuf buf) {
        this(buf.readVarInt(), ClientIntent.byId(buf.readVarInt()));
    }

    private void write(IntelligentByteBuf buf) {
        buf.writeVarInt(this.protocolVersion);
        buf.writeVarInt(this.intention.id());
    }

    @Override
    public PacketType<HandshakeReq> type() {
        return HandshakePacketTypes.HANDSHAKE;
    }

    @Override
    public void handle(ServerHandshakePacketListener listener) {
        listener.handleHandshake(this);
    }

    @Override
    public boolean isTerminal() {
        return true;
    }
}
