package com.hamusuke.numguesser.client.game.phase;

import com.hamusuke.numguesser.client.gui.component.panel.main.play.GamePanel;
import com.hamusuke.numguesser.game.phase.GamePhase;

public interface ClientGamePhase extends GamePhase {
    void onEnter(final GamePanel panel);
}
