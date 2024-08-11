package com.hamusuke.numguesser.game.round;

import com.hamusuke.numguesser.server.network.ServerPlayer;

import javax.annotation.Nullable;
import java.util.List;

public class TwoPlayerGameRound extends GameRound {
    public TwoPlayerGameRound(List<ServerPlayer> players, @Nullable ServerPlayer parent) {
        super(players, parent);
    }

    @Override
    protected int getGivenCardNumPerPlayer() {
        return 4;
    }

    @Override
    protected ServerPlayer nextParent() {
        return null;
    }

    @Override
    public GameRound newRound() {
        return new TwoPlayerGameRound(this.players, this.parent);
    }
}
