package com.hamusuke.numguesser.server.network.listener.main;

import com.hamusuke.numguesser.network.channel.Connection;
import com.hamusuke.numguesser.network.listener.server.main.ServerPlayPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import com.hamusuke.numguesser.network.protocol.packet.common.serverbound.ReadyReq;
import com.hamusuke.numguesser.network.protocol.packet.play.PlayProtocols;
import com.hamusuke.numguesser.network.protocol.packet.play.serverbound.*;
import com.hamusuke.numguesser.server.NumGuesserServer;
import com.hamusuke.numguesser.server.game.PairPlayGame;
import com.hamusuke.numguesser.server.network.ServerPlayer;

public class ServerPlayPacketListenerImpl extends ServerCommonPacketListenerImpl implements ServerPlayPacketListener {
    public ServerPlayPacketListenerImpl(NumGuesserServer server, Connection connection, ServerPlayer player) {
        super(server, connection, player);
        connection.setupInboundProtocol(PlayProtocols.SERVERBOUND, this);
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
            default -> this.handleActions(packet);
        }
    }

    @Override
    public void handleReady(ReadyReq packet) {
        super.handleReady(packet);
        if (this.room.getGame() != null) {
            this.room.getGame().ready();
        }
    }

    protected void handleActions(final Packet<?> packet) {
        if (this.room.getGame() == null) {
            return;
        }

        this.room.getGame().onPlayerAction(this.player, packet);
    }

    @Override
    public void handleCardSelect(CardSelectReq packet) {
        this.handleActions(packet);
    }

    @Override
    public void handleCardForAttackSelect(CardForAttackSelectRsp packet) {
        this.handleActions(packet);
    }

    @Override
    public synchronized void handleToss(TossRsp packet) {
        this.handleActions(packet);
    }

    @Override
    public void handleAttack(AttackReq packet) {
        this.handleActions(packet);
    }

    @Override
    public void handlePairColorChange(PairColorChangeReq packet) {
        if (this.player != this.room.getOwner() || !(this.room.getGame() instanceof PairPlayGame pairPlayGameMode)) {
            return;
        }

        pairPlayGameMode.onPairColorChange(packet);
    }

    @Override
    public void handlePairMakingDone(PairMakingDoneReq packet) {
        if (this.player != this.room.getOwner() || !(this.room.getGame() instanceof PairPlayGame pairPlayGameMode)) {
            return;
        }

        pairPlayGameMode.onPairMakingDone();
    }

    @Override
    public void handleGameExited(GameExitedNotify packet) {
        new ServerRoomPacketListenerImpl(this.server, this.player.connection.getConnection(), this.player);
    }
}
