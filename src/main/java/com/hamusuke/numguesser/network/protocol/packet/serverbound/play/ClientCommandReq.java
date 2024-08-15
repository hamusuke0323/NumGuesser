package com.hamusuke.numguesser.network.protocol.packet.serverbound.play;

import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.listener.server.main.ServerPlayPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;

public record ClientCommandReq(Command command) implements Packet<ServerPlayPacketListener> {
    public ClientCommandReq(IntelligentByteBuf buf) {
        this(buf.readEnum(Command.class));
    }

    @Override
    public void write(IntelligentByteBuf buf) {
        buf.writeEnum(this.command);
    }

    @Override
    public void handle(ServerPlayPacketListener listener) {
        listener.handleClientCommand(this);
    }

    public enum Command {
        EXIT_GAME
    }
}
