package com.hamusuke.numguesser.client.game.round.phase.phases;

import com.hamusuke.numguesser.client.game.ClientGame;
import com.hamusuke.numguesser.client.game.round.phase.ClientGamePhase;
import com.hamusuke.numguesser.client.gui.component.panel.main.play.GamePanel;
import com.hamusuke.numguesser.game.Game;
import com.hamusuke.numguesser.game.phase.phases.SelectCardForAttackPhase;

public class ClientSelectCardForAttackPhase extends SelectCardForAttackPhase implements ClientGamePhase {
    @Override
    public void onEnter(ClientGame game, GamePanel panel) {
        game.clearCardSelection();
        game.getGameData(Game.CURRENT_ATTACKER);
    }
}
