package com.hamusuke.numguesser.client.game.round.phase;

import com.hamusuke.numguesser.client.NumGuesser;
import com.hamusuke.numguesser.client.game.ClientGame;
import com.hamusuke.numguesser.game.Game;
import com.hamusuke.numguesser.network.protocol.packet.play.serverbound.ClientCommandReq;

public interface ClientCancellable {
    default boolean isCancellable(final ClientGame game) {
        return game.getGameData(Game.CANCELLABLE);
    }

    default void onPlayerCancel() {
        final var client = NumGuesser.getInstance();
        client.executeSync(() -> {
            if (client.getConnection() == null) {
                return;
            }

            client.getConnection().sendPacket(new ClientCommandReq(ClientCommandReq.Command.CANCEL));
        });
    }
}
