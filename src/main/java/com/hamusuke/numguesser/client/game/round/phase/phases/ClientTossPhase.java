package com.hamusuke.numguesser.client.game.round.phase.phases;

import com.hamusuke.numguesser.client.NumGuesser;
import com.hamusuke.numguesser.client.game.ClientGame;
import com.hamusuke.numguesser.client.game.card.AbstractClientCard;
import com.hamusuke.numguesser.client.game.round.phase.ClientActable;
import com.hamusuke.numguesser.client.game.round.phase.ClientGamePhase;
import com.hamusuke.numguesser.client.gui.component.panel.main.play.GamePanel;
import com.hamusuke.numguesser.game.Game;
import com.hamusuke.numguesser.game.phase.action.actions.TossAction;
import com.hamusuke.numguesser.game.phase.phases.TossPhase;
import org.jdesktop.swingx.JXButton;
import org.jdesktop.swingx.JXLabel;

import javax.swing.*;
import java.awt.*;

import static com.hamusuke.numguesser.client.gui.component.panel.Panel.addButton;

public class ClientTossPhase extends TossPhase implements ClientGamePhase, ClientActable<TossAction> {
    @Override
    public void onEnter(final ClientGame game, final GamePanel panel) {
        final var tosser = game.getPlayer(game.getGameData(Game.CURRENT_TOSSER));
        if (tosser == null) {
            return;
        }

        final var center = panel.getCenterPanel();
        final var layout = (GridBagLayout) center.getLayout();
        final var client = NumGuesser.getInstance();

        if (tosser != client.clientPlayer) {
            addButton(center, new JXLabel(tosser.getName() + "がトスするカードを選んでいます", SwingConstants.CENTER), layout, 0, 0, 1, 1, 1.0D, 0.05D);
            return;
        }

        addButton(center, new JXLabel("味方にトスするカードを選んでください", SwingConstants.CENTER), layout, 0, 0, 1, 1, 1.0D, 0.05D);

        final var button = new JXButton("このカードをトスする");
        button.addActionListener(e -> this.toss(panel.getSelectedCard(), client));
        addButton(center, button, layout, 0, 1, 1, 1, 1.0D, 0.05D);
    }

    private void toss(final AbstractClientCard selected, final NumGuesser client) {
        if (selected == null || !client.clientPlayer.getDeck().contains(selected) || selected.isOpened()) {
            return;
        }

        this.onClientAction(new TossAction(selected.getId()));
    }
}
