package com.hamusuke.numguesser.server.game.event.events;

import com.hamusuke.numguesser.server.game.card.ServerCard;

import java.util.List;

public record CardsOpenEvent(List<ServerCard> cards) implements GameEvent {
}
