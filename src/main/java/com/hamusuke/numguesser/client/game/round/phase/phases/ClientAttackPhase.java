package com.hamusuke.numguesser.client.game.round.phase.phases;

import com.hamusuke.numguesser.client.game.ClientGame;
import com.hamusuke.numguesser.client.game.round.phase.ClientActable;
import com.hamusuke.numguesser.client.game.round.phase.ClientCancellable;
import com.hamusuke.numguesser.client.game.round.phase.ClientGamePhase;
import com.hamusuke.numguesser.client.gui.component.panel.main.play.GamePanel;
import com.hamusuke.numguesser.game.Game;
import com.hamusuke.numguesser.game.phase.action.actions.AttackActions;
import com.hamusuke.numguesser.game.phase.phases.AttackPhase;

public class ClientAttackPhase extends AttackPhase implements ClientGamePhase, ClientActable<AttackActions>, ClientCancellable {
    @Override
    public void onEnter(final ClientGame game, final GamePanel panel) {
        final var card = game.getGameData(Game.ATTACK_CARD);

    }
}
