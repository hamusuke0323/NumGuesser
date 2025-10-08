package com.hamusuke.numguesser.server.game.seating;

import com.hamusuke.numguesser.server.game.pair.ServerPlayerPair;
import com.hamusuke.numguesser.server.network.ServerPlayer;

import java.util.List;
import java.util.Random;

public class PairPlaySeatingArranger extends SeatingArranger {
    private final ServerPlayerPair bluePair;
    private final ServerPlayerPair redPair;

    public PairPlaySeatingArranger(final ServerPlayerPair bluePair, final ServerPlayerPair redPair) {
        this.bluePair = bluePair;
        this.redPair = redPair;
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
            var pair = i % 2 == 0 ? this.bluePair : this.redPair;
            this.seatingArrangement.set(seatIndex, (i < 2 ? pair.left() : pair.right()).getId());
        }
    }
}
