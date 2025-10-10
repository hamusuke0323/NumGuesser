package com.hamusuke.numguesser.server.game.event.events;

import com.hamusuke.numguesser.game.card.Card;

import java.util.List;

public record CardsOpenEvent(List<? extends Card> cards) implements GameEvent {
}
