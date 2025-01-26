package com.hamusuke.numguesser.network.listener.server.main;

import com.hamusuke.numguesser.network.protocol.packet.serverbound.play.*;

public interface ServerPlayPacketListener extends ServerCommonPacketListener {
    void handleClientCommand(ClientCommandReq packet);

    void handleCardSelect(CardSelectReq packet);

    void handleCardForAttackSelect(CardForAttackSelectRsp packet);

    void handleToss(TossRsp packet);

    void handleAttack(AttackReq packet);

    void handlePairColorChange(PairColorChangeReq packet);

    void handlePairMakingDone(PairMakingDoneReq packet);
}
