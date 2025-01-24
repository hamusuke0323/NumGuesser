package com.hamusuke.numguesser.client.network.player;

import com.hamusuke.numguesser.client.game.card.AbstractClientCard;
import com.hamusuke.numguesser.client.game.card.ClientPlayerDeck;
import com.hamusuke.numguesser.network.Player;

public abstract class AbstractClientPlayer extends Player {
    protected ClientPlayerDeck deck;
    protected AbstractClientCard forAttack;

    protected AbstractClientPlayer(String name) {
        super(name);
    }

    public void setId(int id) {
        this.id = id;
    }

    public ClientPlayerDeck newDeck() {
        this.deck = new ClientPlayerDeck(this);
        return this.deck;
    }

    public void onAttack(AbstractClientCard cardForAttack) {
        this.forAttack = cardForAttack;
    }

    public AbstractClientCard getCardForAttack() {
        return this.forAttack;
    }

    public ClientPlayerDeck getDeck() {
        return this.deck;
    }
}
