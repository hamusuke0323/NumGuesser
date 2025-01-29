package com.hamusuke.numguesser.network.listener.client.main;

import com.hamusuke.numguesser.network.protocol.Protocol;
import com.hamusuke.numguesser.network.protocol.packet.play.clientbound.*;

public interface ClientPlayPacketListener extends ClientCommonPacketListener {
    @Override
    default Protocol protocol() {
        return Protocol.PLAY;
    }

    void handlePairMakingStart(PairMakingStartNotify packet);

    void handlePairColorChange(PairColorChangeNotify packet);

    void handleStartGameRound(StartGameRoundNotify packet);

    void handleSeatingArrangement(SeatingArrangementNotify packet);

    void handlePlayerNewDeck(PlayerNewDeckNotify packet);

    void handlePlayerDeckSync(PlayerDeckSyncNotify packet);

    void handleExitGameSucc(ExitGameSuccNotify packet);

    void handleTossOrAttackSelection(TossOrAttackSelectionNotify packet);

    void handleTossReq(TossReq packet);

    void handleTossNotify(TossNotify packet);

    void handleCardForAttackSelect(CardForAttackSelectReq packet);

    void handleRemotePlayerSelectCardForAttack(RemotePlayerSelectCardForAttackNotify packet);

    void handlePlayerStartAttacking(PlayerStartAttackNotify packet);

    void handleRemotePlayerStartAttacking(RemotePlayerStartAttackNotify packet);

    void handleCardOpen(CardOpenNotify packet);

    void handleCardsOpen(CardsOpenNotify packet);

    void handlePlayerCardSelectionSync(PlayerCardSelectionSyncNotify packet);

    void handleAttack(AttackRsp packet);

    void handlePlayerNewCardAdd(PlayerNewCardAddNotify packet);

    void handleAttackSucc(AttackSuccNotify packet);

    void handleEndGameRound(EndGameRoundNotify packet);
}
