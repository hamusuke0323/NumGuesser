package com.hamusuke.numguesser.client.game.phase;

import com.hamusuke.numguesser.client.gui.component.panel.main.play.GamePanel;
import com.hamusuke.numguesser.game.phase.PhaseType;

public class ClientPreparingGamePhase implements ClientGamePhase {
    @Override
    public void onEnter(final GamePanel panel) {

    }

    @Override
    public PhaseType type() {
        return PhaseType.PREPARE;
    }
}
