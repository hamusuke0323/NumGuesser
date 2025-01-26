package com.hamusuke.numguesser.network.listener.client.main;

import com.hamusuke.numguesser.network.protocol.packet.clientbound.play.*;

public interface ClientPlayPacketListener extends ClientCommonPacketListener {
    void handlePairMakingStart(PairMakingStartNotify packet);

    void handlePairColorChange(PairColorChangeNotify packet);

    void handleStartGameRound(StartGameRoundNotify packet);

    void handleSeatingArrangement(SeatingArrangementNotify packet);

    void handlePlayerNewDeck(PlayerNewDeckNotify packet);

    void handlePlayerDeckSync(PlayerDeckSyncNotify packet);

    void handleExitGameSucc(ExitGameSuccNotify packet);

    void handleCardForAttackSelect(CardForAttackSelectReq packet);

    void handleRemotePlayerSelectCardForAttack(RemotePlayerSelectCardForAttackNotify packet);

    void handlePlayerStartAttacking(PlayerStartAttackingNotify packet);

    void handleRemotePlayerStartAttacking(RemotePlayerStartAttackingNotify packet);

    void handleCardOpen(CardOpenNotify packet);

    void handleCardsOpen(CardsOpenNotify packet);

    void handlePlayerCardSelectionSync(PlayerCardSelectionSyncNotify packet);

    void handleAttack(AttackRsp packet);

    void handlePlayerNewCardAdd(PlayerNewCardAddNotify packet);

    void handleAttackSucc(AttackSuccNotify packet);

    void handleEndGameRound(EndGameRoundNotify packet);
}
