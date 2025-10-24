package com.hamusuke.numguesser.server.game.round.phase;

import com.hamusuke.numguesser.game.phase.GamePhase;
import com.hamusuke.numguesser.server.game.event.events.CardsOpenEvent;
import com.hamusuke.numguesser.server.game.event.events.GameMessageEvent;
import com.hamusuke.numguesser.server.game.event.events.GamePhaseTransitionEvent;
import com.hamusuke.numguesser.server.game.round.GameRound;
import com.hamusuke.numguesser.server.network.ServerPlayer;

public interface ServerGamePhase extends GamePhase {
    default void onEnter(final GameRound round) {
        round.eventBus.post(new GamePhaseTransitionEvent(this));
    }

    default void restart(final GameRound round) {
        this.onEnter(round);
    }

    default void onPlayerLeft(final GameRound round, final ServerPlayer player) {
        round.eventBus.post(new GameMessageEvent(player.getDisplayName() + "がゲームをやめました"));
        final var list = player.getDeck().openAllCards();
        if (!list.isEmpty()) {
            round.eventBus.post(new CardsOpenEvent(list));
        }
        round.parentDeterminer.removeParentCandidate(player);

        if (round.players.size() <= 1) {
            round.setWinner(round.players.isEmpty() ? null : round.players.getFirst());
            round.endRound();
            return;
        }

        player.setReady(false);
        player.setIsDefeated(false);
        round.players.forEach(sp -> sp.setReady(false));
        this.onPlayerLeftPost(round, player);
        if (round.arePlayersDefeatedBy(round.getCurAttacker())) {
            round.setWinner(round.getCurAttacker());
            round.endRound();
        }
    }

    default void onPlayerLeftPost(final GameRound round, final ServerPlayer player) {
    }
}
