package com.hamusuke.numguesser.server.game.round.phase.phases;

import com.hamusuke.numguesser.game.card.Card;
import com.hamusuke.numguesser.server.game.round.GameRound;
import com.hamusuke.numguesser.server.game.round.phase.GamePhase;

public class PullCardPhase implements GamePhase<PullCardPhase.Result> {
    private Result result;

    @Override
    public void onEnter(final GameRound round) {
        if (round.cardRegistry.isEmpty()) {
            this.result = new Result.EmptyDeck();
        } else {
            final var card = round.cardRegistry.pull();
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

        record PulledCard(Card pulledCard) implements Result {
        }
    }
}
