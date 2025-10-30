package com.hamusuke.numguesser.network.listener.client.main;

import com.hamusuke.numguesser.network.protocol.Protocol;
import com.hamusuke.numguesser.network.protocol.packet.play.clientbound.*;

public interface ClientPlayPacketListener extends ClientCommonPacketListener {
    @Override
    default Protocol protocol() {
        return Protocol.PLAY;
    }

    void handlePairColorChange(PairColorChangeNotify packet);

    void handleStartGameRound(StartGameRoundNotify packet);

    void handlePlayerNewDeck(PlayerNewDeckNotify packet);

    void handlePlayerDeckSync(PlayerDeckSyncNotify packet);

    void handleExitGameSucc(ExitGameSuccNotify packet);

    void handleGameDataSync(GameDataSyncNotify packet);

    void handleGamePhaseTransition(GamePhaseTransitionNotify packet);

    void handleTossNotify(TossNotify packet);

    void handleCardOpen(CardOpenNotify packet);

    void handleCardsOpen(CardsOpenNotify packet);

    void handlePlayerCardSelectionSync(PlayerCardSelectionSyncNotify packet);

    void handlePlayerNewCardAdd(PlayerNewCardAddNotify packet);
}
