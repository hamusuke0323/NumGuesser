package com.hamusuke.numguesser.client.game.round.phase;

import com.hamusuke.numguesser.client.game.ClientGame;
import com.hamusuke.numguesser.client.gui.component.panel.main.play.GamePanel;
import com.hamusuke.numguesser.game.phase.GamePhase;

public interface ClientGamePhase extends GamePhase {
    void onEnter(final ClientGame game, final GamePanel panel);

    default void onExit(final ClientGame game, final GamePanel panel) {
        panel.getCenterPanel().removeAll();
    }
}
