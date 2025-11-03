package com.hamusuke.numguesser.server.game;

import com.hamusuke.numguesser.game.GameMode;
import com.hamusuke.numguesser.server.game.data.ServerGameDataRegistry;
import com.hamusuke.numguesser.server.game.event.handler.PacketSender;
import com.hamusuke.numguesser.server.game.pair.ServerPlayerPairRegistry;
import com.hamusuke.numguesser.server.game.seating.PairPlaySeatingArranger;
import com.hamusuke.numguesser.server.game.seating.SeatingArranger;
import com.hamusuke.numguesser.server.network.ServerPlayer;
import com.hamusuke.numguesser.server.room.ServerRoom;
import org.apache.commons.lang3.function.Consumers;

import java.util.List;
import java.util.function.Consumer;

public class GameModeRegistry {
    private static final GameModeRegistry GENERIC =
            Builder.of(ServerGame::new)
                    .configureGame(game -> {
                        game.serverGameData.define(ServerGameDataRegistry.SEATING_ARRANGER, new SeatingArranger());
                        game.eventBus.register(new PacketSender(game.playerList));
                    })
                    .build();
    private static final GameModeRegistry PAIR_PLAY =
            Builder.of(ServerPairPlayGame::new)
                    .configureGame(game -> {
                        final var pairRegistry = new ServerPlayerPairRegistry();
                        game.serverGameData.define(ServerGameDataRegistry.PAIR_REGISTRY, pairRegistry)
                                .define(ServerGameDataRegistry.SEATING_ARRANGER, new PairPlaySeatingArranger(pairRegistry))
                                .define(ServerGameDataRegistry.HAS_MADE_TEAM, false);

                        game.eventBus.register(new PacketSender(game.playerList));
                    })
                    .build();
    private final GameCreator creator;

    private GameModeRegistry(final GameCreator creator) {
        this.creator = creator;
    }

    public static ServerGame create(final GameMode mode, final ServerRoom room, final List<ServerPlayer> players) {
        return switch (mode) {
            case GENERIC -> GENERIC.creator.create(room, players);
            case PAIR_PLAY -> PAIR_PLAY.creator.create(room, players);
        };
    }

    private interface GameCreator {
        ServerGame create(final ServerRoom room, final List<ServerPlayer> players);
    }

    private static class Builder {
        private final GameCreator base;
        private Consumer<ServerGame> gameManipulator = Consumers.nop();

        private Builder(final GameCreator base) {
            this.base = base;
        }

        private static Builder of(final GameCreator base) {
            return new Builder(base);
        }

        private Builder configureGame(final Consumer<ServerGame> gameManipulator) {
            this.gameManipulator = gameManipulator;
            return this;
        }

        private GameModeRegistry build() {
            return new GameModeRegistry((room, players) -> {
                final var game = this.base.create(room, players);
                this.gameManipulator.accept(game);
                return game;
            });
        }
    }
}
