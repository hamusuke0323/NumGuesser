package com.hamusuke.numguesser.client.game.round.phase.phases;

import com.hamusuke.numguesser.client.NumGuesser;
import com.hamusuke.numguesser.client.game.ClientGame;
import com.hamusuke.numguesser.client.game.round.phase.ClientActable;
import com.hamusuke.numguesser.client.game.round.phase.ClientGamePhase;
import com.hamusuke.numguesser.client.gui.component.panel.main.play.GamePanel;
import com.hamusuke.numguesser.game.Game;
import com.hamusuke.numguesser.game.phase.action.actions.SelectTossOrAttackAction;
import com.hamusuke.numguesser.game.phase.phases.SelectTossOrAttackPhase;
import org.jdesktop.swingx.JXButton;
import org.jdesktop.swingx.JXLabel;

import javax.swing.*;
import java.awt.*;

import static com.hamusuke.numguesser.client.gui.component.panel.Panel.addButton;

public class ClientSelectTossOrAttackPhase extends SelectTossOrAttackPhase implements ClientGamePhase, ClientActable<SelectTossOrAttackAction> {
    @Override
    public void onEnter(final ClientGame game, final GamePanel panel) {
        final var attacker = game.getPlayer(game.getGameData(Game.CURRENT_ATTACKER));
        if (attacker == null) {
            return;
        }

        final var center = panel.getCenterPanel();
        final var layout = (GridBagLayout) center.getLayout();
        final var client = NumGuesser.getInstance();

        if (attacker != client.clientPlayer) {
            addButton(center, new JXLabel(attacker.getName() + "がトスかアタックかを選んでいます", SwingConstants.CENTER), layout, 0, 0, 1, 1, 1.0D, 0.05D);
            return;
        }

        final var tossButton = new JXButton("トスしてもらう");
        tossButton.addActionListener(e -> this.onClientAction(new SelectTossOrAttackAction(true)));
        final var attackButton = new JXButton("アタック");
        attackButton.addActionListener(e -> this.onClientAction(new SelectTossOrAttackAction(false)));

        addButton(center, new JXLabel("トスをしてもらうかアタックするかを選んでください", SwingConstants.CENTER), layout, 0, 0, 1, 1, 1.0D, 0.05D);
        addButton(center, tossButton, layout, 0, 1, 1, 1, 1.0D, 0.05D);
        addButton(center, attackButton, layout, 0, 2, 1, 1, 1.0D, 0.05D);
    }
}
