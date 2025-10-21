package com.hamusuke.numguesser.client.event;

import com.hamusuke.numguesser.event.Event;
import com.hamusuke.numguesser.event.EventBus;

public class ClientEventBus extends EventBus<Event> {
    public ClientEventBus() {
        super(Event.class);
    }
}
