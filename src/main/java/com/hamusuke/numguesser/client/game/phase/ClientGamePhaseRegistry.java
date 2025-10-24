package com.hamusuke.numguesser.client.game.phase;

import com.google.common.collect.Maps;
import com.hamusuke.numguesser.game.phase.PhaseType;

import java.util.EnumMap;
import java.util.function.Supplier;

public class ClientGamePhaseRegistry {
    private static final EnumMap<PhaseType, Supplier<ClientGamePhase>> REGISTRY = Maps.newEnumMap(PhaseType.class);

    static {
        register(PhaseType.PREPARE, ClientPreparingGamePhase::new);
        validate();
    }

    private static void register(final PhaseType phaseType, final Supplier<ClientGamePhase> supplier) {
        REGISTRY.put(phaseType, supplier);
    }

    private static void validate() {
        for (final var e : PhaseType.values()) {
            if (!REGISTRY.containsKey(e)) {
                throw new NullPointerException("ClientGamePhaseRegistry#REGISTRY does not contain " + e);
            }
        }
    }

    public static ClientGamePhase newPhaseOf(final PhaseType phaseType) {
        return REGISTRY.get(phaseType).get();
    }
}
