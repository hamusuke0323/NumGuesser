package com.hamusuke.numguesser.server.game.round.phase.phases;

import com.hamusuke.numguesser.game.phase.action.actions.ContinueOrStayAction;
import com.hamusuke.numguesser.game.phase.phases.ContinueOrStayPhase;
import com.hamusuke.numguesser.server.game.card.ServerCard;
import com.hamusuke.numguesser.server.game.event.events.CardOpenEvent;
import com.hamusuke.numguesser.server.game.round.GameRound;
import com.hamusuke.numguesser.server.game.round.phase.Actable;
import com.hamusuke.numguesser.server.game.round.phase.HasResult;
import com.hamusuke.numguesser.server.game.round.phase.ServerGamePhase;
import com.hamusuke.numguesser.server.network.ServerPlayer;

public class ServerContinueOrStayPhase extends ContinueOrStayPhase implements ServerGamePhase, Actable<ContinueOrStayAction>, HasResult<ServerContinueOrStayPhase.Result> {
    private final ServerCard cardForAttacking;
    private Result result;

    public ServerContinueOrStayPhase(final ServerCard cardForAttacking) {
        this.cardForAttacking = cardForAttacking;
    }

    @Override
    public void onPlayerAction(final GameRound round, final ServerPlayer actor, final ContinueOrStayAction action) {
        if (round.getCurAttacker() != actor) {
            return;
        }

        if (action.continueAttacking()) {
            this.result = new Result.Continue(this.cardForAttacking);
        } else {
            round.ownCard(round.getCurAttacker(), this.cardForAttacking);
            round.nextAttacker();
            this.result = new Result.Stay();
        }

        round.nextPhase();
    }

    @Override
    public void onPlayerLeftPost(final GameRound round, final ServerPlayer player) {
        if (round.getCurAttacker() != player) {
            return;
        }

        this.cardForAttacking.open();
        round.ownCard(player, this.cardForAttacking);
        round.nextAttacker();
        round.eventBus.post(new CardOpenEvent(this.cardForAttacking));
        this.result = new Result.Stay();
        round.nextPhase();
    }

    @Override
    public Result getResult() {
        return this.result;
    }

    public sealed interface Result permits ServerContinueOrStayPhase.Result.Continue, ServerContinueOrStayPhase.Result.Stay {
        record Continue(ServerCard cardForAttacking) implements Result {
        }

        record Stay() implements Result {
        }
    }
}
