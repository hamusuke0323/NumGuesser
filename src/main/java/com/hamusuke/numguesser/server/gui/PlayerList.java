package com.hamusuke.numguesser.server.gui;

import com.hamusuke.numguesser.network.Player;
import com.hamusuke.numguesser.server.NumGuesserServer;
import org.jdesktop.swingx.JXList;

import java.util.Vector;

public class PlayerList extends JXList {
    private final NumGuesserServer server;
    private int tickCount;

    public PlayerList(NumGuesserServer server) {
        this.server = server;
        server.addTickable(this::tick);
    }

    public void tick() {
        if (this.tickCount++ % 20 == 0) {
            this.setListData(
                    new Vector<>(
                            this.server.getPlayerManager().getPlayers().stream()
                                    .map(Player::getName)
                                    .toList()));
        }
    }
}
