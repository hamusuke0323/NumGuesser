package com.hamusuke.numguesser.server.network.listener.main;

import com.hamusuke.numguesser.network.channel.Connection;
import com.hamusuke.numguesser.network.listener.server.main.ServerPlayPacketListener;
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
        var game = this.room.getGame();
        if (game == null) {
            return;
        }

        switch (packet.command()) {
            case EXIT_GAME -> this.room.exitGame(this.player);
            case CANCEL -> game.onCancelCommand(this.player);
            case LET_ALLY_TOSS -> game.onTossSelected(this.player);
            case ATTACK_WITHOUT_TOSS -> game.onAttackSelected(this.player);
            case CONTINUE_ATTACKING -> game.continueAttacking(this.player);
            case STAY -> game.stay(this.player);
        }
    }

    @Override
    public void handleReady(ReadyReq packet) {
        super.handleReady(packet);
        if (this.room.getGame() != null) {
            this.room.getGame().ready();
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
    public void handleCardForAttackSelect(CardForAttackSelectRsp packet) {
        if (this.room.getGame() == null) {
            return;
        }

        this.room.getGame().onCardForAttackSelect(this.player, packet.id());
    }

    @Override
    public synchronized void handleToss(TossRsp packet) {
        if (this.room.getGame() == null) {
            return;
        }

        this.room.getGame().onToss(this.player, packet.cardId());
    }

    @Override
    public void handleAttack(AttackReq packet) {
        if (this.room.getGame() == null) {
            return;
        }

        this.room.getGame().onAttack(this.player, packet.id(), packet.num());
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
