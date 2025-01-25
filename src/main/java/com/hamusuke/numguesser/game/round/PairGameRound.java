package com.hamusuke.numguesser.game.round;

import com.hamusuke.numguesser.game.NumGuesserGame;
import com.hamusuke.numguesser.server.network.ServerPlayer;

import javax.annotation.Nullable;
import java.util.List;

public class PairGameRound extends GameRound {
    public PairGameRound(NumGuesserGame game, List<ServerPlayer> players, @Nullable ServerPlayer parent) {
        super(game, players, parent);
    }

    @Override
    protected int getGivenCardNumPerPlayer() {
        return 6;
    }

    @Override
    public GameRound newRound() {
        var game = new PairGameRound(this.game, this.players, this.parent);
        game.pulledCardMapForDecidingParent.putAll(this.pulledCardMapForDecidingParent);
        return game;
    }
}
