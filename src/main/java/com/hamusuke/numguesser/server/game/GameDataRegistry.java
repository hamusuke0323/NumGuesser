package com.hamusuke.numguesser.server.game;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public class GameDataRegistry {
    private static final AtomicInteger CURRENT_ID = new AtomicInteger();
    private static final Supplier<Integer> NEXT_ID = CURRENT_ID::getAndIncrement;
    public static final int SEATING_ARRANGER = register();
    public static final int PAIR_REGISTRY = register();
    public static final int HAS_MADE_TEAM = register();

    private static int register() {
        return NEXT_ID.get();
    }
}
