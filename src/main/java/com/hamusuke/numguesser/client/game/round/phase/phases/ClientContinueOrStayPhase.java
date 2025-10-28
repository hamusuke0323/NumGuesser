package com.hamusuke.numguesser.client.game.round.phase.phases;

import com.hamusuke.numguesser.client.NumGuesser;
import com.hamusuke.numguesser.client.game.ClientGame;
import com.hamusuke.numguesser.client.game.card.LocalCard;
import com.hamusuke.numguesser.client.game.round.phase.ClientActable;
import com.hamusuke.numguesser.client.game.round.phase.ClientGamePhase;
import com.hamusuke.numguesser.client.gui.component.panel.main.play.GamePanel;
import com.hamusuke.numguesser.game.Game;
import com.hamusuke.numguesser.game.phase.action.actions.ContinueOrStayAction;
import com.hamusuke.numguesser.game.phase.phases.ContinueOrStayPhase;
import org.jdesktop.swingx.JXButton;
import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXPanel;

import javax.swing.*;
import java.awt.*;

import static com.hamusuke.numguesser.client.gui.component.panel.Panel.addButton;

public class ClientContinueOrStayPhase extends ContinueOrStayPhase implements ClientGamePhase, ClientActable<ContinueOrStayAction> {
    @Override
    public void onEnter(ClientGame game, GamePanel panel) {
        final var attacker = game.getPlayer(game.getGameData(Game.CURRENT_ATTACKER));
        if (attacker == null) {
            return;
        }

        final var center = panel.getCenterPanel();
        final var layout = (GridBagLayout) center.getLayout();

        // the remote player attacks.
        if (attacker != NumGuesser.getInstance().clientPlayer) {
            addButton(center, new JXLabel(attacker.getName() + "がアタックしています", SwingConstants.CENTER), layout, 0, 0, 1, 1, 1.0D, 0.05D);
            return;
        }

        // your turn
        final var statusLabel = new JXLabel("アタック成功です。アタックを続けるかステイするかを選んでください", SwingConstants.CENTER);
        final var cardCase = new JXPanel();
        final var card = game.getGameData(Game.ATTACK_CARD);
        cardCase.add(LocalCard.from(card.serializer()).toPanel(), BorderLayout.CENTER);
        final var continueButton = new JXButton("アタックを続ける");
        continueButton.addActionListener(e -> this.onClientAction(new ContinueOrStayAction(true)));
        final var stayButton = new JXButton("ステイ");
        stayButton.addActionListener(e -> this.onClientAction(new ContinueOrStayAction(false)));

        addButton(center, statusLabel, layout, 0, 0, 1, 1, 1.0D, 0.05D);
        addButton(center, cardCase, layout, 0, 1, 1, 1, 1.0D);
        addButton(center, continueButton, layout, 0, 2, 1, 1, 1.0D, 0.05D);
        addButton(center, stayButton, layout, 0, 3, 1, 1, 1.0D, 0.05D);
    }
}
