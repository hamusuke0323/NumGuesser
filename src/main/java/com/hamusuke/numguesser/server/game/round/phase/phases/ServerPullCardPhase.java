package com.hamusuke.numguesser.server.game.round.phase.phases;

import com.hamusuke.numguesser.game.phase.phases.PullCardPhase;
import com.hamusuke.numguesser.server.game.card.ServerCard;
import com.hamusuke.numguesser.server.game.round.GameRound;
import com.hamusuke.numguesser.server.game.round.phase.HasResult;
import com.hamusuke.numguesser.server.game.round.phase.ServerGamePhase;

public class ServerPullCardPhase extends PullCardPhase implements ServerGamePhase, HasResult<ServerPullCardPhase.Result> {
    private Result result;

    @Override
    public void onEnter(final GameRound round) {
        if (round.cardRegistry.isEmpty()) {
            this.result = new Result.EmptyDeck();
        } else {
            final var card = round.cardRegistry.pull();
            card.setOwner(round.getCurAttacker());
            this.result = new Result.PulledCard(card);
        }

        round.nextPhase();
    }

    @Override
    public Result getResult() {
        return this.result;
    }

    public sealed interface Result permits Result.EmptyDeck, Result.PulledCard {
        record EmptyDeck() implements Result {
        }

        record PulledCard(ServerCard pulledCard) implements Result {
        }
    }
}
