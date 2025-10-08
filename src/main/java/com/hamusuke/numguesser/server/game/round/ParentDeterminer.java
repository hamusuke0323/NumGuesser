package com.hamusuke.numguesser.server.game.round;

import com.google.common.collect.Maps;
import com.hamusuke.numguesser.game.card.Card;
import com.hamusuke.numguesser.server.network.ServerPlayer;

import java.util.List;
import java.util.Map;

public class ParentDeterminer {
    private final Map<ServerPlayer, Card> pulledCardMap = Maps.newHashMap();
    private ServerPlayer parent;

    public void determineParentPermutationIfNeeded(final List<ServerPlayer> players, final CardRegistry cardRegistry) {
        if (this.parent != null) {
            return;
        }

        this.pulledCardMap.clear();
        for (var player : players) {
            this.pulledCardMap.put(player, cardRegistry.peek(player.getRandom()));
        }

        this.next();
    }

    public ServerPlayer next() {
        this.pulledCardMap.entrySet().stream()
                .min(Map.Entry.comparingByValue())
                .ifPresent(e -> this.parent = e.getKey());
        this.pulledCardMap.remove(this.parent);

        return this.parent;
    }

    public void removeParentCandidate(final ServerPlayer player) {
        this.pulledCardMap.remove(player);
    }

    public boolean hasNoCandidates() {
        return this.pulledCardMap.isEmpty();
    }

    public ServerPlayer getCurrentParent() {
        return this.parent;
    }
}
