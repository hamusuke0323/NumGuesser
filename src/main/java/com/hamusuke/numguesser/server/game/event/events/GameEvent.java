package com.hamusuke.numguesser.server.game.event.events;

public sealed interface GameEvent permits CardOpenEvent, CardsOpenEvent, GameMessageEvent, GameRoundEndEvent, GameRoundStartEvent, PairColorChangeEvent, PairMakingStartEvent, PlayerEvent, SeatingArrangementEvent {
}
