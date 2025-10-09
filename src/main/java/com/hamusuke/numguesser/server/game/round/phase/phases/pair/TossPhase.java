package com.hamusuke.numguesser.server.game.round.phase.phases.pair;

import com.hamusuke.numguesser.network.protocol.packet.common.clientbound.ChatNotify;
import com.hamusuke.numguesser.network.protocol.packet.play.clientbound.TossNotify;
import com.hamusuke.numguesser.server.game.event.events.PlayerSelectCardForTossEvent;
import com.hamusuke.numguesser.server.game.round.GameRound;
import com.hamusuke.numguesser.server.game.round.phase.ActableGamePhase;
import com.hamusuke.numguesser.server.game.round.phase.action.actions.TossAction;
import com.hamusuke.numguesser.server.network.ServerPlayer;

public class TossPhase implements ActableGamePhase<TossAction, Void> {
    private final ServerPlayer buddy;

    public TossPhase(final ServerPlayer buddy) {
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

        round.getCurAttacker().sendPacket(new TossNotify(card.toSerializer()));
        round.getCurAttacker().sendPacket(new ChatNotify("味方があなたにトスしました"));
        round.nextPhase();
    }
}
