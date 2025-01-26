package com.hamusuke.numguesser.game.round;

import com.hamusuke.numguesser.game.mode.PairPlayGameMode;
import com.hamusuke.numguesser.server.game.ServerPlayerPair;
import com.hamusuke.numguesser.server.network.ServerPlayer;

import javax.annotation.Nullable;
import java.util.List;

public class PairGameRound extends GameRound {
    private final ServerPlayerPair bluePair;
    private final ServerPlayerPair redPair;

    public PairGameRound(PairPlayGameMode game, List<ServerPlayer> players, @Nullable ServerPlayer parent) {
        super(game, players, parent);
        this.bluePair = game.getBluePair();
        this.redPair = game.getRedPair();
    }

    @Override
    protected void startAttacking() {
        super.startAttacking();
    }

    @Override
    protected void setSeatingArrangement() {
        super.setSeatingArrangement(); // invoke super method to replace them after
        int startIndex = this.random.nextInt(4); // first seat is selected randomly.

        // seating permutation is like this:
        // one of blue pair, one of red pair, the other of blue pair, and the other of red pair.
        for (int i = 0; i < this.players.size(); i++) {
            int seatIndex = (i + startIndex) % this.players.size();
            var pair = i % 2 == 0 ? this.bluePair : this.redPair;
            this.seatingArrangement.set(seatIndex, (i < 2 ? pair.left() : pair.right()).getId());
        }
    }

    @Override
    protected int getGivenCardNumPerPlayer() {
        return 6;
    }

    @Override
    public GameRound newRound() {
        var game = new PairGameRound((PairPlayGameMode) this.game, this.players, this.parent);
        game.pulledCardMapForDecidingParent.putAll(this.pulledCardMapForDecidingParent);
        return game;
    }
}
