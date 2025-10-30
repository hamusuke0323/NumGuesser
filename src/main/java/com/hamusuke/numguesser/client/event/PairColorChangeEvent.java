package com.hamusuke.numguesser.client.event;

import com.hamusuke.numguesser.client.network.player.AbstractClientPlayer;
import com.hamusuke.numguesser.event.Event;
import com.hamusuke.numguesser.game.pair.PlayerPair;

public record PairColorChangeEvent(AbstractClientPlayer player, PlayerPair.PairColor color) implements Event {
}
