package com.hamusuke.numguesser.server.game.data;

import com.hamusuke.numguesser.server.game.pair.ServerPlayerPairRegistry;
import com.hamusuke.numguesser.server.game.seating.SeatingArranger;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public class ServerGameDataRegistry {
    private static final AtomicInteger CURRENT_ID = new AtomicInteger();
    private static final Supplier<Integer> NEXT_ID = CURRENT_ID::getAndIncrement;
    public static final DataKey<SeatingArranger> SEATING_ARRANGER = register();
    public static final DataKey<ServerPlayerPairRegistry> PAIR_REGISTRY = register();
    public static final DataKey<Boolean> HAS_MADE_TEAM = register();

    private static <T> DataKey<T> register() {
        return new DataKey<>(NEXT_ID.get());
    }

    public record DataKey<T>(int id) {
    }
}
