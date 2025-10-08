package com.hamusuke.numguesser.server.game.pair;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.hamusuke.numguesser.game.pair.PlayerPair;
import com.hamusuke.numguesser.server.network.ServerPlayer;

import java.util.List;
import java.util.Map;

public class ServerPlayerPairRegistry {
    private final Map<PlayerPair.PairColor, ServerPlayerPair> pairs = Maps.newConcurrentMap();

    public ServerPlayerPairRegistry() {
        for (final var color : PlayerPair.PairColor.values()) {
            this.pairs.put(color, new ServerPlayerPair(color));
        }
    }

    public ServerPlayerPair get(PlayerPair.PairColor color) {
        return this.pairs.get(color);
    }

    public ServerPlayerPair get(final ServerPlayer player) {
        return this.get(player.getPairColor());
    }

    public ServerPlayer getBuddyFor(final ServerPlayer player) {
        return this.get(player).getBuddyFor(player);
    }

    public List<ServerPlayer> getPlayers(final PlayerPair.PairColor color) {
        return this.get(color).getPlayers();
    }

    public Map<ServerPlayer, PlayerPair.PairColor> toPlayer2ColorMap() {
        Map<ServerPlayer, PlayerPair.PairColor> pairMap = Maps.newHashMap();
        this.pairs.values().stream()
                .map(PlayerPair::toMap)
                .forEach(pairMap::putAll);
        return ImmutableMap.copyOf(pairMap);
    }
}
