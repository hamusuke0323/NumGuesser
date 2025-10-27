package com.hamusuke.numguesser.server.game.round.phase.phases;

import com.hamusuke.numguesser.game.phase.action.actions.SelectTossOrAttackAction;
import com.hamusuke.numguesser.game.phase.phases.SelectTossOrAttackPhase;
import com.hamusuke.numguesser.server.game.event.events.PlayerSelectTossOrAttackEvent;
import com.hamusuke.numguesser.server.game.round.GameRound;
import com.hamusuke.numguesser.server.game.round.PairGameRound;
import com.hamusuke.numguesser.server.game.round.phase.Actable;
import com.hamusuke.numguesser.server.game.round.phase.HasResult;
import com.hamusuke.numguesser.server.game.round.phase.ServerGamePhase;
import com.hamusuke.numguesser.server.network.ServerPlayer;

public class ServerSelectTossOrAttackPhase extends SelectTossOrAttackPhase implements ServerGamePhase, Actable<SelectTossOrAttackAction>, HasResult<ServerSelectTossOrAttackPhase.Result> {
    private Result result;

    @Override
    public void onEnter(final GameRound round) {
        round.eventBus.post(new PlayerSelectTossOrAttackEvent(round.getCurAttacker()));
    }

    @Override
    public void onPlayerAction(final GameRound round, final ServerPlayer actor, final SelectTossOrAttackAction action) {
        if (round.getCurAttacker() != actor) {
            return;
        }

        if (action.isToss()) {
            final var buddy = ((PairGameRound) round).pairRegistry.getBuddyFor(actor);
            this.result = buddy.isDefeated() ? new Result.BuddyAlreadyDefeated() : new Result.Toss(buddy); // Check: if the buddy is defeated, toss is not allowed.
        } else {
            this.result = new Result.Attack();
        }

        round.nextPhase();
    }

    @Override
    public Result getResult() {
        return this.result;
    }

    public sealed interface Result {
        record BuddyAlreadyDefeated() implements Result {
        }

        record Toss(ServerPlayer buddy) implements Result {
        }

        record Attack() implements Result {
        }
    }
}
