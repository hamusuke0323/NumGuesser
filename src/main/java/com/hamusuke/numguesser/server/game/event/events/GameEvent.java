package com.hamusuke.numguesser.server.game.event.events;

import com.hamusuke.numguesser.event.Event;

public sealed interface GameEvent extends Event permits CardOpenEvent, CardsOpenEvent, GameMessageEvent, GamePhaseTransitionEvent, GameRoundEndEvent, GameRoundStartEvent, PairColorChangeEvent, PairMakingStartEvent, PlayerEvent, SeatingArrangementEvent, TossEvent {
}
