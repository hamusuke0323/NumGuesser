package com.hamusuke.numguesser.client.network.player;

import com.hamusuke.numguesser.network.Player;

public abstract class AbstractClientPlayer extends Player {
    protected AbstractClientPlayer(String name) {
        super(name);
    }

    public void setId(int id) {
        this.id = id;
    }
}
