package com.hamusuke.numguesser.network.listener.client.main;

import com.hamusuke.numguesser.network.protocol.packet.clientbound.play.PlayerDeckSyncNotify;

public interface ClientPlayPacketListener extends ClientCommonPacketListener {
    void handlePlayerDeckSync(PlayerDeckSyncNotify packet);
}
