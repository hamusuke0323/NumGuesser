package com.hamusuke.numguesser.client.game.round.phase.phases;

import com.hamusuke.numguesser.client.NumGuesser;
import com.hamusuke.numguesser.client.game.ClientGame;
import com.hamusuke.numguesser.client.game.round.phase.ClientActable;
import com.hamusuke.numguesser.client.game.round.phase.ClientCancellable;
import com.hamusuke.numguesser.client.game.round.phase.ClientGamePhase;
import com.hamusuke.numguesser.client.gui.component.panel.main.play.GamePanel;
import com.hamusuke.numguesser.game.Game;
import com.hamusuke.numguesser.game.phase.action.actions.SelectCardForAttackAction;
import com.hamusuke.numguesser.game.phase.phases.SelectCardForAttackPhase;
import org.jdesktop.swingx.JXButton;
import org.jdesktop.swingx.JXLabel;

import javax.swing.*;
import java.awt.*;

import static com.hamusuke.numguesser.client.gui.component.panel.Panel.addButton;

public class ClientSelectCardForAttackPhase extends SelectCardForAttackPhase implements ClientGamePhase, ClientActable<SelectCardForAttackAction>, ClientCancellable {
    @Override
    public void onEnter(final ClientGame game, final GamePanel panel) {
        game.clearCardSelection();
        final var attacker = game.getPlayer(game.getGameData(Game.CURRENT_ATTACKER));
        if (attacker == null) {
            return;
        }

        final var center = panel.getCenterPanel();
        final var layout = (GridBagLayout) center.getLayout();

        // the remote player is about to attack.
        if (attacker != NumGuesser.getInstance().clientPlayer) {
            addButton(center, new JXLabel(attacker.getName() + "がアタックするためのカードを選んでいます", SwingConstants.CENTER), layout, 0, 0, 1, 1, 1.0D, 0.05D);
            return;
        }

        // your turn
        final var statusLabel = new JXLabel("ふせたカードの中から、アタックするためのカードを選んでください", SwingConstants.CENTER);
        final var button = new JXButton("このカードでアタックする");
        button.addActionListener(e -> this.selectThisCardForAttack(panel, statusLabel, button));

        addButton(center, statusLabel, layout, 0, 0, 1, 1, 1.0D, 0.05D);
        addButton(center, button, layout, 0, 1, 1, 1, 1.0D, 0.05D);

        if (this.isCancellable(game)) {
            final var cancel = new JXButton("キャンセル");
            cancel.addActionListener(e -> this.onPlayerCancel());
            addButton(center, cancel, layout, 0, 2, 1, 1, 1.0D, 0.05D);
        }
    }

    private void selectThisCardForAttack(final GamePanel panel, final JXLabel statusLabel, final JXButton button) {
        final var selectedCard = panel.getSelectedCard();
        if (selectedCard == null || !NumGuesser.getInstance().clientPlayer.getDeck().contains(selectedCard) || selectedCard.isOpened()) {
            return;
        }

        statusLabel.setText("カードをサーバーに送信しています...");
        button.setVisible(false);
        this.onClientAction(new SelectCardForAttackAction(selectedCard.getId()));
    }
}
