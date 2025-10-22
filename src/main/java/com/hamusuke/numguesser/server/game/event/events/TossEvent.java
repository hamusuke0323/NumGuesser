package com.hamusuke.numguesser.server.game.event.events;

import com.hamusuke.numguesser.server.game.card.ServerCard;
import com.hamusuke.numguesser.server.network.ServerPlayer;

public record TossEvent(ServerPlayer attacker, ServerCard card) implements GameEvent {
}
