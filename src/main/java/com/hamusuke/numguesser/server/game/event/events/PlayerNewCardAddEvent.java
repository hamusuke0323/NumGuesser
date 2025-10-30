package com.hamusuke.numguesser.server.game.event.events;

import com.hamusuke.numguesser.server.game.card.ServerCard;
import com.hamusuke.numguesser.server.network.ServerPlayer;

public final class PlayerNewCardAddEvent extends PlayerEvent {
    private final int index;
    private final ServerCard card;

    public PlayerNewCardAddEvent(ServerPlayer player, int index, ServerCard card) {
        super(player);
        this.index = index;
        this.card = card;
    }

    public int getIndex() {
        return this.index;
    }

    public ServerCard getCard() {
        return this.card;
    }
}
