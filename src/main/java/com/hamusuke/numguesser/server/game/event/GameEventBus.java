package com.hamusuke.numguesser.server.game.event;

import com.hamusuke.numguesser.event.EventBus;
import com.hamusuke.numguesser.server.game.event.events.GameEvent;

public class GameEventBus extends EventBus<GameEvent> {
    public GameEventBus() {
        super(GameEvent.class);
    }
}
