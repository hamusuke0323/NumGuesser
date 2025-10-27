package com.hamusuke.numguesser.server.game.round.phase.phases;

import com.hamusuke.numguesser.game.phase.action.actions.TossAction;
import com.hamusuke.numguesser.game.phase.phases.TossPhase;
import com.hamusuke.numguesser.server.game.event.events.PlayerSelectCardForTossEvent;
import com.hamusuke.numguesser.server.game.event.events.TossEvent;
import com.hamusuke.numguesser.server.game.round.GameRound;
import com.hamusuke.numguesser.server.game.round.phase.Actable;
import com.hamusuke.numguesser.server.game.round.phase.ServerGamePhase;
import com.hamusuke.numguesser.server.network.ServerPlayer;

public class ServerTossPhase extends TossPhase implements ServerGamePhase, Actable<TossAction> {
    private final ServerPlayer buddy;

    public ServerTossPhase(final ServerPlayer buddy) {
        this.buddy = buddy;
    }

    @Override
    public void onEnter(final GameRound round) {
        round.eventBus.post(new PlayerSelectCardForTossEvent(this.buddy));
    }

    @Override
    public void onPlayerAction(final GameRound round, final ServerPlayer tosser, final TossAction action) {
        final var cardOwner = round.cardRegistry.getCardOwnerById(action.cardId());
        final var card = round.cardRegistry.getOwnedCardById(action.cardId()); // card == null should never happen
        if (cardOwner != tosser || card == null) {
            return;
        }

        round.eventBus.post(new TossEvent(round.getCurAttacker(), card));
        round.nextPhase();
    }
}
