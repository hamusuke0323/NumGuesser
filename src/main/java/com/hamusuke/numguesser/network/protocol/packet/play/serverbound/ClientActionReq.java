package com.hamusuke.numguesser.network.protocol.packet.play.serverbound;

import com.hamusuke.numguesser.game.phase.action.ActionDataSerializers;
import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.codec.StreamCodec;
import com.hamusuke.numguesser.network.listener.server.main.ServerPlayPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import com.hamusuke.numguesser.network.protocol.packet.PacketType;
import com.hamusuke.numguesser.network.protocol.packet.play.PlayPacketTypes;

public record ClientActionReq(Object data) implements Packet<ServerPlayPacketListener> {
    public static final StreamCodec<IntelligentByteBuf, ClientActionReq> STREAM_CODEC = StreamCodec.ofMember(ClientActionReq::write, ClientActionReq::new);

    private ClientActionReq(final IntelligentByteBuf buf) {
        this(ActionDataSerializers.<Object>readFrom(buf));
    }

    private void write(final IntelligentByteBuf buf) {
        ActionDataSerializers.writeTo(buf, this.data);
    }

    @Override
    public void handle(ServerPlayPacketListener listener) {
        listener.handleClientAction(this);
    }

    @Override
    public PacketType<? extends Packet<ServerPlayPacketListener>> type() {
        return PlayPacketTypes.CLIENT_ACTION;
    }
}
