package com.hamusuke.numguesser.network.listener.server.main;

import com.hamusuke.numguesser.network.protocol.packet.serverbound.play.AttackReq;
import com.hamusuke.numguesser.network.protocol.packet.serverbound.play.CardForAttackSelectRsp;
import com.hamusuke.numguesser.network.protocol.packet.serverbound.play.CardSelectReq;
import com.hamusuke.numguesser.network.protocol.packet.serverbound.play.ClientCommandReq;

public interface ServerPlayPacketListener extends ServerCommonPacketListener {
    void handleClientCommand(ClientCommandReq packet);

    void handleCardSelect(CardSelectReq packet);

    void handleCardForAttackSelect(CardForAttackSelectRsp packet);

    void handleAttack(AttackReq packet);
}
