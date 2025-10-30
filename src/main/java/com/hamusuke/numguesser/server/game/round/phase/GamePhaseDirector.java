package com.hamusuke.numguesser.server.game.round.phase;

import com.hamusuke.numguesser.server.game.round.phase.phases.*;

public class GamePhaseDirector {
    public static GamePhaseManager forNormalGame() {
        return GamePhaseManager.Builder.builder()
                .startWith(ServerPreparingGamePhase::new)
                .advanceAfter(ServerPreparingGamePhase.class, ServerPullCardPhase::new)
                .advanceWithResultAfter(ServerPullCardPhase.class, result -> switch (result) {
                    case ServerPullCardPhase.Result.EmptyDeck __ -> new ServerSelectCardForAttackPhase();
                    case ServerPullCardPhase.Result.PulledCard(final var card) -> new ServerAttackPhase(card);
                })
                .advanceWithResultAfter(ServerSelectCardForAttackPhase.class, card -> card == null ? new ServerPullCardPhase() : new ServerAttackPhase(true, card))
                .advanceWithResultAfter(ServerAttackPhase.class, result -> switch (result) {
                    case ServerAttackPhase.Result.Success(final var card) -> new ServerContinueOrStayPhase(card);
                    case ServerAttackPhase.Result.Failure __ -> new ServerPullCardPhase();
                })
                .advanceWithResultAfter(ServerContinueOrStayPhase.class, result -> switch (result) {
                    case ServerContinueOrStayPhase.Result.Continue(final var card) -> new ServerAttackPhase(true, card);
                    case ServerContinueOrStayPhase.Result.Stay __ -> new ServerPullCardPhase();
                })
                .endWith(ServerEndPhase::new)
                .build();
    }

    public static GamePhaseManager forPairPlayGame() {
        return GamePhaseManager.Builder.builder()
                .startWith(ServerPairMakingPhase::new)
                .advanceAfter(ServerPairMakingPhase.class, ServerPreparingGamePhase::new)
                .advanceAfter(ServerPreparingGamePhase.class, ServerCheckBuddyAlivePhase::new)
                .advanceWithResultAfter(ServerCheckBuddyAlivePhase.class, isAlive -> isAlive ? new ServerSelectTossOrAttackPhase() : new ServerSelectCardForAttackPhase())
                .advanceWithResultAfter(ServerSelectTossOrAttackPhase.class, result -> switch (result) {
                    case ServerSelectTossOrAttackPhase.Result.BuddyAlreadyDefeated __ ->
                            new ServerSelectCardForAttackPhase();
                    case ServerSelectTossOrAttackPhase.Result.Toss(final var buddy) -> new ServerTossPhase(buddy);
                    case ServerSelectTossOrAttackPhase.Result.Attack __ -> new ServerSelectCardForAttackPhase(true);
                })
                .advanceAfter(ServerTossPhase.class, ServerSelectCardForAttackPhase::new)
                .advanceWithResultAfter(ServerSelectCardForAttackPhase.class, card -> card == null ? new ServerCheckBuddyAlivePhase() : new ServerPairAttackPhase(true, card))
                .advanceWithResultAfter(ServerPairAttackPhase.class, result -> switch (result) {
                    case ServerAttackPhase.Result.Success(final var card) -> new ServerContinueOrStayPhase(card);
                    case ServerAttackPhase.Result.Failure __ -> new ServerCheckBuddyAlivePhase();
                })
                .advanceWithResultAfter(ServerContinueOrStayPhase.class, result -> switch (result) {
                    case ServerContinueOrStayPhase.Result.Continue(final var card) ->
                            new ServerPairAttackPhase(true, card);
                    case ServerContinueOrStayPhase.Result.Stay __ -> new ServerCheckBuddyAlivePhase();
                })
                .endWith(ServerPairEndPhase::new)
                .build();
    }
}
