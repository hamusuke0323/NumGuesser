package com.hamusuke.numguesser.client.game.single;

import com.hamusuke.numguesser.client.NumGuesser;

public abstract class SinglePlayerGameController {
    protected final NumGuesser client;

    public SinglePlayerGameController(NumGuesser client) {
        this.client = client;
    }

    public abstract void putCard(int listIndex, int cardIndex);


}
