package com.hamusuke.numguesser.client.event;

import com.hamusuke.numguesser.client.game.card.AbstractClientCard;
import com.hamusuke.numguesser.event.Event;

public record CardSelectEvent(AbstractClientCard card) implements Event {
}
