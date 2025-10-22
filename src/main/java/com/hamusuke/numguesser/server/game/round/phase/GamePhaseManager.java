package com.hamusuke.numguesser.server.game.round.phase;

import com.google.common.collect.Maps;
import com.hamusuke.numguesser.server.game.round.GameRound;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public class GamePhaseManager {
    private final Supplier<GamePhase<?>> start;
    private final Supplier<GamePhase<?>> end;
    private final Map<Class<? extends GamePhase<?>>, Function<?, GamePhase<?>>> phaseTransitionMap;
    @Nullable
    private GamePhase<?> prevPhase;
    private GamePhase<?> currentPhase;

    private GamePhaseManager(final Supplier<GamePhase<?>> start, final Supplier<GamePhase<?>> end, final Map<Class<? extends GamePhase<?>>, Function<?, GamePhase<?>>> phaseTransitionMap) {
        this.start = start;
        this.end = end;
        this.phaseTransitionMap = phaseTransitionMap;
    }

    public void start(final GameRound round) {
        this.prevPhase = null;
        this.currentPhase = this.start.get();
        this.currentPhase.onEnter(round);
    }

    public void prev(final GameRound round) {
        if (this.prevPhase == null) {
            throw new IllegalStateException("prevPhase is null");
        }

        this.currentPhase = this.prevPhase;
        this.currentPhase.onEnter(round);
        this.prevPhase = null;
    }

    public void next(final GameRound round) {
        if (!this.hasNext()) {
            throw new IllegalStateException("nextPhaseMap has no mapping for " + this.currentPhase.getClass());
        }

        final Function nextPhaseFunc = this.phaseTransitionMap.get(this.currentPhase.getClass());
        final var nextPhase = nextPhaseFunc.apply(this.currentPhase.getResult());
        if (nextPhase == null) {
            throw new NullPointerException("nextPhaseFunc returned null");
        }

        this.prevPhase = this.currentPhase;
        this.currentPhase = (GamePhase<?>) nextPhase;
        this.currentPhase.onEnter(round);
    }

    public void setEndForcibly(final GameRound round) {
        this.currentPhase = this.end.get();
        this.currentPhase.onEnter(round);
    }

    public boolean hasNext() {
        return this.phaseTransitionMap.containsKey(this.currentPhase.getClass());
    }

    public GamePhase<?> getCurrentPhase() {
        return this.currentPhase;
    }

    public static class Builder {
        private final Map<Class<? extends GamePhase<?>>, Function<?, GamePhase<?>>> phaseTransitionMap = Maps.newHashMap();
        private Supplier<GamePhase<?>> start;
        private Supplier<GamePhase<?>> end;

        private Builder() {
        }

        public static Builder builder() {
            return new Builder();
        }

        public Builder startWith(final Supplier<GamePhase<?>> supplier) {
            this.start = supplier;
            return this;
        }

        public <R> Builder advanceAfter(final Class<? extends GamePhase<R>> after, final Supplier<GamePhase<?>> supplier) {
            return this.advanceWithResultAfter(after, __ -> supplier.get());
        }

        public <R> Builder advanceWithResultAfter(final Class<? extends GamePhase<R>> after, final Function<R, GamePhase<?>> function) {
            if (this.phaseTransitionMap.containsKey(after)) {
                throw new IllegalStateException("nextPhaseMap has already mapping for " + after);
            }

            this.phaseTransitionMap.put(after, function);
            return this;
        }

        public Builder endWith(final Supplier<GamePhase<?>> end) {
            this.end = end;
            return this;
        }

        public GamePhaseManager build() {
            if (this.start == null || this.end == null) {
                throw new IllegalStateException("start or end is null");
            }

            return new GamePhaseManager(this.start, this.end, Map.copyOf(this.phaseTransitionMap));
        }
    }
}
