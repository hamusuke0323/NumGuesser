package com.hamusuke.numguesser.server.game.round.phase.phases;

import com.google.common.collect.Lists;
import com.hamusuke.numguesser.game.Game;
import com.hamusuke.numguesser.game.pair.PlayerPair;
import com.hamusuke.numguesser.game.phase.action.actions.PairMakingActions;
import com.hamusuke.numguesser.game.phase.phases.PairMakingPhase;
import com.hamusuke.numguesser.network.Player;
import com.hamusuke.numguesser.server.game.ServerGameDataRegistry;
import com.hamusuke.numguesser.server.game.event.events.PairColorChangeEvent;
import com.hamusuke.numguesser.server.game.pair.ServerPlayerPairRegistry;
import com.hamusuke.numguesser.server.game.round.GameRound;
import com.hamusuke.numguesser.server.game.round.phase.Actable;
import com.hamusuke.numguesser.server.game.round.phase.ServerGamePhase;
import com.hamusuke.numguesser.server.network.ServerPlayer;
import com.hamusuke.numguesser.util.Util;

import java.util.Collections;

public class ServerPairMakingPhase extends PairMakingPhase implements ServerGamePhase, Actable<PairMakingActions> {
    private static void makePairRandomly(final GameRound round) {
        final var random = round.game.room.getOwner().getRandom();
        final var copied = Lists.newArrayList(round.players);
        Collections.shuffle(copied, random);

        final var pairRegistry = round.game.<ServerPlayerPairRegistry>getServerGameData(ServerGameDataRegistry.PAIR_REGISTRY);
        for (int i = 0; i < round.players.size(); i++) {
            final var pair = pairRegistry.get(i % 2 == 0 ? PlayerPair.PairColor.BLUE : PlayerPair.PairColor.RED);
            if (i < 2) {
                pair.left(copied.get(i));
            } else {
                pair.right(copied.get(i));
            }
        }
    }

    @Override
    public void syncGameData(final GameRound round) {
        final var pairRegistry = round.game.<ServerPlayerPairRegistry>getServerGameData(ServerGameDataRegistry.PAIR_REGISTRY);
        final var pairMap = Util.transformToNewImmutableMapOnlyKeys(pairRegistry.toPlayer2ColorMap(), Player::getId);
        round.game.setSyncedData(Game.PAIR_MAP, pairMap);
    }

    @Override
    public void onEnter(final GameRound round) {
        if (round.game.<Boolean>getServerGameData(ServerGameDataRegistry.HAS_MADE_TEAM)) {
            round.nextPhase();
            return;
        }

        makePairRandomly(round);
        ServerGamePhase.super.onEnter(round);
    }

    @Override
    public void onPlayerAction(final GameRound round, final ServerPlayer actor, final PairMakingActions action) {
        if (round.game.<Boolean>getServerGameData(ServerGameDataRegistry.HAS_MADE_TEAM) || round.game.room.getOwner() != actor) {
            return;
        }

        switch (action) {
            case PairMakingActions.PairColorChange pairColorChange -> {
                final var player = round.game.room.getPlayer(pairColorChange.getPlayerId());
                if (player == null) {
                    return;
                }

                player.setPairColor(pairColorChange.getColor());
                round.eventBus.post(new PairColorChangeEvent(player, pairColorChange.getColor()));
            }
            case PairMakingActions.PairMakingDone ignored -> {
                final var pairRegistry = round.game.<ServerPlayerPairRegistry>getServerGameData(ServerGameDataRegistry.PAIR_REGISTRY);
                for (final var color : PlayerPair.PairColor.values()) {
                    final var players = round.players.stream().filter(player -> player.getPairColor() == color).toList();
                    if (players.size() != 2) {
                        return;
                    }

                    final var pair = pairRegistry.get(color);
                    pair.left(players.get(0));
                    pair.right(players.get(1));
                }

                round.game.setSyncedData(Game.PAIR_MAP, Util.transformToNewImmutableMapOnlyKeys(pairRegistry.toPlayer2ColorMap(), Player::getId));
                round.game.setServerGameData(ServerGameDataRegistry.HAS_MADE_TEAM, true);
                round.nextPhase();
            }
        }
    }
}
