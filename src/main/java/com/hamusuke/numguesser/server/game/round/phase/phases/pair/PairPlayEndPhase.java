package com.hamusuke.numguesser.server.game.round.phase.phases.pair;

import com.hamusuke.numguesser.game.card.Card;
import com.hamusuke.numguesser.game.pair.PlayerPair;
import com.hamusuke.numguesser.game.phase.PhaseType;
import com.hamusuke.numguesser.network.Player;
import com.hamusuke.numguesser.server.game.event.events.GameMessageEvent;
import com.hamusuke.numguesser.server.game.round.GameRound;
import com.hamusuke.numguesser.server.game.round.PairGameRound;
import com.hamusuke.numguesser.server.game.round.phase.phases.EndPhase;

public class PairPlayEndPhase extends EndPhase {
    @Override
    protected void giveTipToRoundWinner(final GameRound round) {
        if (round.getWinner() == null) {
            return;
        }

        final var pair = ((PairGameRound) round).pairRegistry.get(round.getWinner());
        final var buddy = pair.getBuddyFor(round.getWinner());
        final int point = round.getWinner().getDeck().getCards().stream()
                .mapToInt(Card::getPoint)
                .sum() +
                buddy.getDeck().getCards().stream()
                        .mapToInt(Card::getPoint)
                        .sum();

        round.getWinner().addTipPoint(point);
        buddy.addTipPoint(point);

        // the defeated pair give tip to the won pair.
        for (final var player : round.players) {
            if (player == round.getWinner() || player == buddy) {
                continue;
            }

            player.subTipPoint(point);
        }
    }

    @Override
    protected void showWonMessage(final GameRound round) {
        final var pairRegistry = ((PairGameRound) round).pairRegistry;
        final int bluePairPoints = pairRegistry.getPlayers(PlayerPair.PairColor.BLUE).stream()
                .mapToInt(Player::getTipPoint)
                .sum();
        final int redPairPoints = pairRegistry.getPlayers(PlayerPair.PairColor.RED).stream()
                .mapToInt(Player::getTipPoint)
                .sum();

        if (bluePairPoints == redPairPoints) {
            round.eventBus.post(new GameMessageEvent("どちらのペアも得点が同じなのでドローです"));
            return;
        }

        final var wonPair = pairRegistry.get(bluePairPoints > redPairPoints ? PlayerPair.PairColor.BLUE : PlayerPair.PairColor.RED);
        round.eventBus.post(new GameMessageEvent("合計" + Math.max(bluePairPoints, redPairPoints) + "点で " + wonPair.left().getDisplayName() + " と " + wonPair.right().getDisplayName() + " が勝利しました"));
    }

    @Override
    public PhaseType type() {
        return PhaseType.PAIR_END;
    }
}
