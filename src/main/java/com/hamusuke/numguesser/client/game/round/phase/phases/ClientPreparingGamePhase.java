package com.hamusuke.numguesser.client.game.round.phase.phases;

import com.hamusuke.numguesser.client.game.ClientGame;
import com.hamusuke.numguesser.client.game.round.phase.ClientGamePhase;
import com.hamusuke.numguesser.client.gui.component.panel.main.play.GamePanel;
import com.hamusuke.numguesser.game.phase.phases.PreparingGamePhase;

public class ClientPreparingGamePhase extends PreparingGamePhase implements ClientGamePhase {
    @Override
    public void onEnter(final ClientGame game, final GamePanel panel) {
    }
}
