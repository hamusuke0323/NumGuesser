package com.hamusuke.numguesser.server.network.listener.main;

import com.hamusuke.numguesser.network.channel.Connection;
import com.hamusuke.numguesser.network.listener.server.main.ServerPlayPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.clientbound.play.AttackRsp;
import com.hamusuke.numguesser.network.protocol.packet.serverbound.play.AttackReq;
import com.hamusuke.numguesser.network.protocol.packet.serverbound.play.CardSelectReq;
import com.hamusuke.numguesser.network.protocol.packet.serverbound.play.ClientCommandReq;
import com.hamusuke.numguesser.server.NumGuesserServer;
import com.hamusuke.numguesser.server.network.ServerPlayer;

public class ServerPlayPacketListenerImpl extends ServerCommonPacketListenerImpl implements ServerPlayPacketListener {
    public ServerPlayPacketListenerImpl(NumGuesserServer server, Connection connection, ServerPlayer player) {
        super(server, connection, player);
    }

    @Override
    public void handleClientCommand(ClientCommandReq packet) {
        switch (packet.command()) {
            case EXIT_GAME -> this.room.exitGame(this.player);
        }
    }

    @Override
    public void handleCardSelect(CardSelectReq packet) {
        if (this.room.getGame() == null) {
            return;
        }

        this.room.getGame().onCardSelect(this.player, packet.id());
    }

    @Override
    public void handleAttack(AttackReq packet) {
        if (this.room.getGame() == null) {
            return;
        }

        this.room.getGame().onAttack(this.player, packet.id(), packet.num());
    }
}
