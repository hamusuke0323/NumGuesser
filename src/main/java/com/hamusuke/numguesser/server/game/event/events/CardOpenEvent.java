package com.hamusuke.numguesser.server.game.event.events;

import com.hamusuke.numguesser.server.game.card.ServerCard;

public record CardOpenEvent(ServerCard card) implements GameEvent {
}
