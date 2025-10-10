package com.hamusuke.numguesser.server.game.round.phase.phases;

import com.hamusuke.numguesser.server.game.event.events.GameMessageEvent;
import com.hamusuke.numguesser.server.game.event.events.PlayerDeckSyncEvent;
import com.hamusuke.numguesser.server.game.event.events.SeatingArrangementEvent;
import com.hamusuke.numguesser.server.game.round.GameRound;
import com.hamusuke.numguesser.server.game.round.phase.GamePhase;
import com.hamusuke.numguesser.server.network.ServerPlayer;

public class PreparingGamePhase implements GamePhase<Void> {
    @Override
    public void onEnter(final GameRound round) {
        round.parentDeterminer.determineParentPermutationIfNeeded(round.players, round.cardRegistry);
        final var parent = round.parentDeterminer.getCurrentParent();
        round.setWinner(parent);
        round.setCurAttacker(parent);
        round.eventBus.post(new GameMessageEvent("親は " + parent.getName() + " に決まりました"));
        round.eventBus.post(new GameMessageEvent("親がカードを配ります"));
        round.eventBus.post(new SeatingArrangementEvent(round.seatingArranger.getSeatingArrangement()));
        this.giveOutCards(round);
        round.nextPhase();
    }

    private void giveOutCards(final GameRound round) {
        round.cardRegistry.shuffle(round.parentDeterminer.getCurrentParent().getRandom());
        round.players.forEach(ServerPlayer::makeNewDeck);

        for (final var player : round.players) {
            for (int i = 0; i < round.getGivenCardNumPerPlayer(); i++) {
                if (round.cardRegistry.isEmpty()) {
                    break;
                }

                player.getDeck().addCard(round.cardRegistry.pullBy(player));
            }

            round.eventBus.post(new PlayerDeckSyncEvent(player));
        }
    }
}
