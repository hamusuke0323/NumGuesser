package com.hamusuke.numguesser.server.game.seating;

import com.google.common.collect.Lists;
import com.hamusuke.numguesser.server.network.ServerPlayer;

import java.util.List;

public class SeatingArranger {
    protected final List<Integer> seatingArrangement = Lists.newArrayList();

    public void arrange(final List<ServerPlayer> players) {
        this.seatingArrangement.clear();
        this.seatingArrangement.addAll(players.stream().map(ServerPlayer::getId).toList());
    }

    public int getSeatIndex(final ServerPlayer player) {
        return this.seatingArrangement.indexOf(player.getId());
    }

    public int get(final int index) {
        return this.seatingArrangement.get(index);
    }

    public int size() {
        return this.seatingArrangement.size();
    }

    public List<Integer> getSeatingArrangement() {
        return List.copyOf(this.seatingArrangement);
    }
}
