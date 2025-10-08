package com.hamusuke.numguesser.server.game.round;

import com.google.common.collect.Maps;
import com.hamusuke.numguesser.game.card.Card;
import com.hamusuke.numguesser.server.network.ServerPlayer;

import java.util.List;
import java.util.Map;

public class ParentDeterminer {
    protected final Map<ServerPlayer, Card> pulledCardMap = Maps.newHashMap();
    protected ServerPlayer parent;

    public ParentDeterminer(final ServerPlayer initial) {
        this.parent = initial;
    }

    public void determine(final List<ServerPlayer> players, final CardRegistry cardRegistry) {
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

    public void copyFrom(final ParentDeterminer old) {
        this.pulledCardMap.clear();
        this.pulledCardMap.putAll(old.pulledCardMap);
        this.parent = old.parent;
    }

    public ServerPlayer getCurrentParent() {
        return this.parent;
    }
}
