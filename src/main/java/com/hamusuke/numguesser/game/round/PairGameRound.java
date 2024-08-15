package com.hamusuke.numguesser.game.round;

import com.hamusuke.numguesser.server.network.ServerPlayer;

import javax.annotation.Nullable;
import java.util.List;

public class PairGameRound extends GameRound {
    public PairGameRound(List<ServerPlayer> players, @Nullable ServerPlayer parent) {
        super(players, parent);
    }

    @Override
    protected int getGivenCardNumPerPlayer() {
        return 6;
    }

    @Override
    public GameRound newRound() {
        var game = new PairGameRound(this.players, this.parent);
        game.pulledCardMap.putAll(this.pulledCardMap);
        return game;
    }
}
