package com.hamusuke.numguesser.server.game;

import com.hamusuke.numguesser.game.GameMode;
import com.hamusuke.numguesser.server.game.event.GameEventBus;
import com.hamusuke.numguesser.server.game.event.handler.PacketSender;
import com.hamusuke.numguesser.server.game.pair.ServerPlayerPairRegistry;
import com.hamusuke.numguesser.server.game.seating.PairPlaySeatingArranger;
import com.hamusuke.numguesser.server.game.seating.SeatingArranger;
import com.hamusuke.numguesser.server.network.ServerPlayer;
import com.hamusuke.numguesser.server.room.ServerRoom;
import org.apache.commons.lang3.function.Consumers;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class GameModeRegistry {
    public static final GameModeRegistry GENERIC =
            Builder.of(ServerGenericGame::new)
                    .defineGameData(map -> {
                        map.put(GameDataRegistry.SEATING_ARRANGER, new SeatingArranger());
                    })
                    .withEventBus((game, eventBus) -> {
                        eventBus.register(new PacketSender(game.playerList));
                    })
                    .build();
    public static final GameModeRegistry PAIR_PLAY =
            Builder.of(ServerPairPlayGame::new)
                    .defineGameData(map -> {
                        final var pairRegistry = new ServerPlayerPairRegistry();
                        map.put(GameDataRegistry.PAIR_REGISTRY, pairRegistry);
                        map.put(GameDataRegistry.SEATING_ARRANGER, new PairPlaySeatingArranger(pairRegistry));
                        map.put(GameDataRegistry.HAS_MADE_TEAM, false);
                    })
                    .withEventBus((game, eventBus) -> {
                        eventBus.register(new PacketSender(game.playerList));
                    })
                    .build();
    private final GameCreator creator;

    private GameModeRegistry(final GameCreator creator) {
        this.creator = creator;
    }

    public static ServerGenericGame create(final GameMode mode, final ServerRoom room, final List<ServerPlayer> players) {
        return switch (mode) {
            case GENERIC -> GENERIC.creator.create(room, players);
            case PAIR_PLAY -> PAIR_PLAY.creator.create(room, players);
        };
    }

    public interface GameCreator {
        ServerGenericGame create(final ServerRoom room, final List<ServerPlayer> players);
    }

    public static class Builder {
        private final GameCreator base;
        private Consumer<Map<Integer, Object>> gameDataDefiner = Consumers.nop();
        private BiConsumer<ServerGenericGame, GameEventBus> eventBusConsumer = (serverGenericGame, gameEventBus) -> {
        };

        private Builder(final GameCreator base) {
            this.base = base;
        }

        public static Builder of(final GameCreator base) {
            return new Builder(base);
        }

        public Builder defineGameData(final Consumer<Map<Integer, Object>> gameDataDefiner) {
            this.gameDataDefiner = gameDataDefiner;
            return this;
        }

        public Builder withEventBus(final BiConsumer<ServerGenericGame, GameEventBus> eventBusConsumer) {
            this.eventBusConsumer = eventBusConsumer;
            return this;
        }

        public GameModeRegistry build() {
            return new GameModeRegistry((room, players) -> {
                final var game = this.base.create(room, players);
                game.defineData(this.gameDataDefiner);
                this.eventBusConsumer.accept(game, game.eventBus);
                return game;
            });
        }
    }
}
