package com.hamusuke.numguesser.network.protocol.packet.play.serverbound;

import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.codec.StreamCodec;
import com.hamusuke.numguesser.network.listener.server.main.ServerPlayPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import com.hamusuke.numguesser.network.protocol.packet.PacketType;
import com.hamusuke.numguesser.network.protocol.packet.play.PlayPacketTypes;

public record ClientCommandReq(Command command) implements Packet<ServerPlayPacketListener> {
    public static final StreamCodec<IntelligentByteBuf, ClientCommandReq> STREAM_CODEC = Packet.codec(ClientCommandReq::write, ClientCommandReq::new);

    private ClientCommandReq(IntelligentByteBuf buf) {
        this(buf.readEnum(Command.class));
    }

    private void write(IntelligentByteBuf buf) {
        buf.writeEnum(this.command);
    }

    @Override
    public PacketType<ClientCommandReq> type() {
        return PlayPacketTypes.CLIENT_COMMAND;
    }

    @Override
    public void handle(ServerPlayPacketListener listener) {
        listener.handleClientCommand(this);
    }

    public enum Command {
        EXIT_GAME,
        CANCEL,
    }
}
