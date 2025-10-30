package com.hamusuke.numguesser.server.game.round.phase.phases;

import com.hamusuke.numguesser.game.Game;
import com.hamusuke.numguesser.game.phase.phases.PreparingGamePhase;
import com.hamusuke.numguesser.server.game.event.events.GameMessageEvent;
import com.hamusuke.numguesser.server.game.event.events.PlayerDeckSyncEvent;
import com.hamusuke.numguesser.server.game.round.GameRound;
import com.hamusuke.numguesser.server.game.round.phase.ServerGamePhase;
import com.hamusuke.numguesser.server.network.ServerPlayer;

public class ServerPreparingGamePhase extends PreparingGamePhase implements ServerGamePhase {
    @Override
    public void onEnter(final GameRound round) {
        ServerGamePhase.super.onEnter(round);
        round.parentDeterminer.determineParentPermutationIfNeeded(round.players, round.cardRegistry);
        final var parent = round.parentDeterminer.getCurrentParent();
        round.setWinner(parent);
        round.setCurAttacker(parent);
        round.game.setSyncedData(Game.CURRENT_ATTACKER, parent.getId());
        round.eventBus.post(new GameMessageEvent("親は " + parent.getName() + " に決まりました"));
        round.eventBus.post(new GameMessageEvent("親がカードを配ります"));
        round.seatingArranger.arrange(round.players);
        round.game.setSyncedData(Game.SEATING_ARRANGEMENT, round.seatingArranger.getSeatingArrangement());
        //round.eventBus.post(new SeatingArrangementEvent(round.seatingArranger.getSeatingArrangement()));
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
