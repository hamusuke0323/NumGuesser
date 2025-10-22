package com.hamusuke.numguesser.server.game.round.phase.phases;

import com.hamusuke.numguesser.game.card.Card;
import com.hamusuke.numguesser.game.phase.PhaseType;
import com.hamusuke.numguesser.network.Player;
import com.hamusuke.numguesser.server.game.event.events.CardsOpenEvent;
import com.hamusuke.numguesser.server.game.event.events.GameMessageEvent;
import com.hamusuke.numguesser.server.game.event.events.GameRoundEndEvent;
import com.hamusuke.numguesser.server.game.round.GameRound;
import com.hamusuke.numguesser.server.game.round.phase.Actable;
import com.hamusuke.numguesser.server.game.round.phase.ServerGamePhase;
import com.hamusuke.numguesser.server.network.ServerPlayer;

import java.util.Comparator;

public class EndPhase implements ServerGamePhase, Actable<Void> {
    @Override
    public void onEnter(final GameRound round) {
        round.eventBus.post(new GameMessageEvent("ラウンド終了"));
        this.giveTipToRoundWinner(round);

        for (final var player : round.players) {
            final var list = player.getDeck().openAllCards();
            if (list.isEmpty()) {
                continue;
            }

            round.eventBus.post(new CardsOpenEvent(list));
        }

        final boolean isFinal = round.isLastRound();
        round.eventBus.post(new GameRoundEndEvent(isFinal));

        if (isFinal) {
            this.endFinalRound(round);
        }
    }

    protected void giveTipToRoundWinner(final GameRound round) {
        if (round.getWinner() == null) {
            return;
        }

        final int point = round.getWinner().getDeck().getCards().stream()
                .mapToInt(Card::getPoint)
                .sum();

        round.getWinner().addTipPoint(point);

        // all defeated players give tip to the winner.
        for (final var player : round.players) {
            if (player == round.getWinner()) {
                continue;
            }

            player.subTipPoint(point);
        }
    }

    protected void endFinalRound(final GameRound round) {
        this.showWonMessage(round);
        round.game.onFinalRoundEnded();
    }

    protected void showWonMessage(final GameRound round) {
        round.players.stream().max(Comparator.comparingInt(Player::getTipPoint)).ifPresent(winner -> {
            round.eventBus.post(new GameMessageEvent(winner.getDisplayName() + " が" + winner.getTipPoint() + "点で勝利しました！"));
        });
    }

    @Override
    public PhaseType type() {
        return PhaseType.END;
    }

    @Override
    public void onPlayerAction(final GameRound round, final ServerPlayer ignored, Void ignored2) {
        if (round.isLastRound()) {
            return;
        }

        if (round.players.stream().allMatch(Player::isReady)) {
            round.players.forEach(player -> player.setReady(false));
            round.game.startNextRound();
        }
    }
}
