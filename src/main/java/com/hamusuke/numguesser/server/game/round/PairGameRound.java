package com.hamusuke.numguesser.server.game.round;

import com.hamusuke.numguesser.server.game.ServerGameDataRegistry;
import com.hamusuke.numguesser.server.game.ServerPairPlayGame;
import com.hamusuke.numguesser.server.game.pair.ServerPlayerPairRegistry;
import com.hamusuke.numguesser.server.game.round.phase.GamePhaseDirector;
import com.hamusuke.numguesser.server.network.ServerPlayer;

import javax.annotation.Nullable;
import java.util.List;

public class PairGameRound extends GameRound {
    public final ServerPlayerPairRegistry pairRegistry;

    public PairGameRound(ServerPairPlayGame game, List<ServerPlayer> players) {
        super(game, players, GamePhaseDirector.forPairPlayGame());
        this.pairRegistry = game.getServerGameData(ServerGameDataRegistry.PAIR_REGISTRY);
    }

    protected PairGameRound(final PairGameRound old) {
        super(old);
        this.pairRegistry = old.pairRegistry;
    }

    @Override
    public boolean arePlayersDefeatedBy(@Nullable ServerPlayer player) {
        return this.players.stream()
                .filter(sp -> !sp.equals(player) && !sp.equals(this.pairRegistry.getBuddyFor(player)))
                .allMatch(ServerPlayer::isDefeated);
    }

    @Override
    public int getGivenCardNumPerPlayer() {
        return 6;
    }

    @Override
    public GameRound newRound() {
        return new PairGameRound(this);
    }
}
