package com.hamusuke.numguesser.server.game.seating;

import com.hamusuke.numguesser.game.pair.PlayerPair;
import com.hamusuke.numguesser.server.game.pair.ServerPlayerPairRegistry;
import com.hamusuke.numguesser.server.network.ServerPlayer;

import java.util.List;
import java.util.Random;

public class PairPlaySeatingArranger extends SeatingArranger {
    private final ServerPlayerPairRegistry pairRegistry;

    public PairPlaySeatingArranger(final ServerPlayerPairRegistry pairRegistry) {
        this.pairRegistry = pairRegistry;
    }

    @Override
    public void arrange(List<ServerPlayer> players) {
        super.arrange(players);

        final var random = new Random();
        final int startIndex = random.nextInt(4); // the first seat is selected randomly.

        // seating permutation is like this:
        // one of the blue pair, one of the red pair, the other of the blue pair, and the other of the red pair.
        for (int i = 0; i < players.size(); i++) {
            int seatIndex = (i + startIndex) % players.size();
            final var color = i % 2 == 0 ? PlayerPair.PairColor.BLUE : PlayerPair.PairColor.RED;
            final var pair = this.pairRegistry.get(color);
            this.seatingArrangement.set(seatIndex, (i < 2 ? pair.left() : pair.right()).getId());
        }
    }
}
