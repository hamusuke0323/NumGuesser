package com.hamusuke.numguesser.server.game.event.events;

import com.hamusuke.numguesser.event.Event;

public sealed interface GameEvent extends Event permits CardOpenEvent, CardsOpenEvent, GameMessageEvent, GameRoundEndEvent, GameRoundStartEvent, PairColorChangeEvent, PairMakingStartEvent, PlayerEvent, SeatingArrangementEvent {
}
