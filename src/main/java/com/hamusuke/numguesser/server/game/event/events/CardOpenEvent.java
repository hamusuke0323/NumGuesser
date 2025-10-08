package com.hamusuke.numguesser.server.game.event.events;

import com.hamusuke.numguesser.game.card.Card;

public record CardOpenEvent(Card card) implements GameEvent {
}
