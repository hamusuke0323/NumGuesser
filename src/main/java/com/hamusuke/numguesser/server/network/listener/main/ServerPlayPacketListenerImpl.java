package com.hamusuke.numguesser.server.network.listener.main;

import com.hamusuke.numguesser.network.channel.Connection;
import com.hamusuke.numguesser.network.listener.server.main.ServerPlayPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.play.PlayProtocols;
import com.hamusuke.numguesser.network.protocol.packet.play.serverbound.ClientActionReq;
import com.hamusuke.numguesser.network.protocol.packet.play.serverbound.ClientCommandReq;
import com.hamusuke.numguesser.network.protocol.packet.play.serverbound.GameExitedNotify;
import com.hamusuke.numguesser.server.NumGuesserServer;
import com.hamusuke.numguesser.server.network.ServerPlayer;

public class ServerPlayPacketListenerImpl extends ServerCommonPacketListenerImpl implements ServerPlayPacketListener {
    public ServerPlayPacketListenerImpl(NumGuesserServer server, Connection connection, ServerPlayer player) {
        super(server, connection, player);
        connection.setupInboundProtocol(PlayProtocols.SERVERBOUND, this);
    }

    @Override
    public void handleClientAction(final ClientActionReq packet) {
        if (this.room.getGame() == null) {
            return;
        }

        this.room.getGame().onPlayerAction(this.player, packet.data());
    }

    @Override
    public synchronized void handleClientCommand(ClientCommandReq packet) {
        final var game = this.room.getGame();
        if (game == null) {
            return;
        }

        switch (packet.command()) {
            case EXIT_GAME -> this.room.exitGame(this.player);
            case CANCEL -> game.onCancelCommand(this.player);
        }
    }

    @Override
    public void handleGameExited(GameExitedNotify packet) {
        new ServerRoomPacketListenerImpl(this.server, this.player.connection.getConnection(), this.player);
    }
}
