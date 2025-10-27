package com.hamusuke.numguesser.network.listener.server.main;

import com.hamusuke.numguesser.network.protocol.Protocol;
import com.hamusuke.numguesser.network.protocol.packet.play.serverbound.*;

public interface ServerPlayPacketListener extends ServerCommonPacketListener {
    @Override
    default Protocol protocol() {
        return Protocol.PLAY;
    }

    void handleClientAction(ClientActionReq packet);

    void handleClientCommand(ClientCommandReq packet);

    void handleCardSelect(CardSelectReq packet);

    void handleCardForAttackSelect(CardForAttackSelectRsp packet);

    void handleToss(TossRsp packet);

    void handleAttack(AttackReq packet);

    void handlePairColorChange(PairColorChangeReq packet);

    void handlePairMakingDone(PairMakingDoneReq packet);

    void handleGameExited(GameExitedNotify packet);
}
