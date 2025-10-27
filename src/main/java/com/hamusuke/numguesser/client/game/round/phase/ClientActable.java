package com.hamusuke.numguesser.client.game.round.phase;

import com.hamusuke.numguesser.client.NumGuesser;
import com.hamusuke.numguesser.network.protocol.packet.play.serverbound.ClientActionReq;

public interface ClientActable<A> {
    default void onClientAction(final A action) {
        final var client = NumGuesser.getInstance();
        client.executeSync(() -> {
            if (client.getConnection() == null) {
                return;
            }

            client.getConnection().sendPacket(new ClientActionReq(action));
        });
    }
}
