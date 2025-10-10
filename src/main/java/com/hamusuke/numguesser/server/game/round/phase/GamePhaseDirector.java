package com.hamusuke.numguesser.server.game.round.phase;

import com.hamusuke.numguesser.server.game.round.phase.phases.*;
import com.hamusuke.numguesser.server.game.round.phase.phases.pair.*;

public class GamePhaseDirector {
    public static GamePhaseManager forNormalGame() {
        return GamePhaseManager.Builder.builder()
                .start(PreparingGamePhase::new)
                .advanceAfter(PreparingGamePhase.class, PullCardPhase::new)
                .advanceWithResultAfter(PullCardPhase.class, result -> switch (result) {
                    case PullCardPhase.Result.EmptyDeck _ -> new SelectCardForAttackPhase();
                    case PullCardPhase.Result.PulledCard(final var card) -> new AttackPhase(card);
                })
                .advanceWithResultAfter(SelectCardForAttackPhase.class, card -> card == null ? new PullCardPhase() : new AttackPhase(true, card))
                .advanceWithResultAfter(AttackPhase.class, result -> switch (result) {
                    case AttackPhase.Result.Success(final var card) -> new ContinueOrStayPhase(card);
                    case AttackPhase.Result.Failure _ -> new PullCardPhase();
                })
                .advanceWithResultAfter(ContinueOrStayPhase.class, result -> switch (result) {
                    case ContinueOrStayPhase.Result.Continue(final var card) -> new AttackPhase(true, card);
                    case ContinueOrStayPhase.Result.Stay _ -> new PullCardPhase();
                })
                .endWith(EndPhase::new)
                .build();
    }

    public static GamePhaseManager forPairPlayGame() {
        return GamePhaseManager.Builder.builder()
                .start(PreparingGamePhase::new)
                .advanceAfter(PreparingGamePhase.class, CheckBuddyAlivePhase::new)
                .advanceWithResultAfter(CheckBuddyAlivePhase.class, isAlive -> isAlive ? new SelectTossOrAttackPhase() : new SelectCardForAttackPhase())
                .advanceWithResultAfter(SelectTossOrAttackPhase.class, result -> switch (result) {
                    case SelectTossOrAttackPhase.Result.BuddyAlreadyDefeated _ -> new SelectCardForAttackPhase();
                    case SelectTossOrAttackPhase.Result.Toss(final var buddy) -> new TossPhase(buddy);
                    case SelectTossOrAttackPhase.Result.Attack _ -> new SelectCardForAttackPhase(true);
                })
                .advanceAfter(TossPhase.class, SelectCardForAttackPhase::new)
                .advanceWithResultAfter(SelectCardForAttackPhase.class, card -> card == null ? new CheckBuddyAlivePhase() : new PairPlayAttackPhase(true, card))
                .advanceWithResultAfter(PairPlayAttackPhase.class, result -> switch (result) {
                    case AttackPhase.Result.Success(final var card) -> new ContinueOrStayPhase(card);
                    case AttackPhase.Result.Failure _ -> new CheckBuddyAlivePhase();
                })
                .advanceWithResultAfter(ContinueOrStayPhase.class, result -> switch (result) {
                    case ContinueOrStayPhase.Result.Continue(final var card) -> new PairPlayAttackPhase(true, card);
                    case ContinueOrStayPhase.Result.Stay _ -> new CheckBuddyAlivePhase();
                })
                .endWith(PairPlayEndPhase::new)
                .build();
    }
}
