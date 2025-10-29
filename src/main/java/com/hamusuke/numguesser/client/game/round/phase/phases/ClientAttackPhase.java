package com.hamusuke.numguesser.client.game.round.phase.phases;

import com.hamusuke.numguesser.client.NumGuesser;
import com.hamusuke.numguesser.client.event.CardSelectEvent;
import com.hamusuke.numguesser.client.game.ClientGame;
import com.hamusuke.numguesser.client.game.card.AbstractClientCard;
import com.hamusuke.numguesser.client.game.card.LocalCard;
import com.hamusuke.numguesser.client.game.round.phase.ClientActable;
import com.hamusuke.numguesser.client.game.round.phase.ClientCancellable;
import com.hamusuke.numguesser.client.game.round.phase.ClientGamePhase;
import com.hamusuke.numguesser.client.gui.component.list.CardList;
import com.hamusuke.numguesser.client.gui.component.panel.main.play.GamePanel;
import com.hamusuke.numguesser.event.EventHandler;
import com.hamusuke.numguesser.game.Game;
import com.hamusuke.numguesser.game.phase.action.actions.AttackActions;
import com.hamusuke.numguesser.game.phase.phases.AttackPhase;
import org.jdesktop.swingx.JXButton;
import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXPanel;

import javax.swing.*;
import java.awt.*;

import static com.hamusuke.numguesser.client.gui.component.panel.Panel.addButton;

public class ClientAttackPhase extends AttackPhase implements ClientGamePhase, ClientActable<AttackActions>, ClientCancellable {
    @Override
    public void onEnter(final ClientGame game, final GamePanel panel) {
        game.clearCardSelection();
        final var attacker = game.getPlayer(game.getGameData(Game.CURRENT_ATTACKER));
        if (attacker == null) {
            return;
        }

        final var card = AbstractClientCard.from(game.getGameData(Game.ATTACK_CARD).serializer());
        final var client = NumGuesser.getInstance();
        final var center = panel.getCenterPanel();
        final var layout = (GridBagLayout) center.getLayout();

        // the remote player attacks.
        if (attacker != client.clientPlayer) {
            attacker.onAttack(card);
            addButton(center, new JXLabel(attacker.getName() + "がアタックしています", SwingConstants.CENTER), layout, 0, 0, 1, 1, 1.0D, 0.05D);
            return;
        }

        // your turn
        final var statusLabel = new JXLabel("あなたの番です。アタックしてください", SwingConstants.CENTER);
        final var cardCase = new JXPanel();
        cardCase.add(card.toPanel(), BorderLayout.CENTER);
        final var button = new JXButton("アタック");
        button.addActionListener(e -> this.showAttackDialog(panel));

        addButton(center, statusLabel, layout, 0, 0, 1, 1, 1.0D, 0.05D);
        addButton(center, cardCase, layout, 0, 1, 1, 1, 1.0D);
        addButton(center, button, layout, 0, 2, 1, 1, 1.0D, 0.05D);

        if (this.isCancellable(game)) {
            final var cancel = new JXButton("キャンセル");
            cancel.addActionListener(e -> this.onPlayerCancel());
            addButton(center, cancel, layout, 0, 3, 1, 1, 1.0D, 0.05D);
        }

        client.eventBus.register(this);
        final var selected = panel.getSelectedCard();
        if (selected != null) {
            this.onClientAction(new AttackActions.Select(selected.getId()));
        }
    }

    private void showAttackDialog(final GamePanel panel) {
        final var client = NumGuesser.getInstance();
        final var card = panel.getSelectedCard();
        if (card == null || card.isOpened() || client.clientPlayer.getDeck().contains(card)) {
            return;
        }

        final var model = new DefaultListModel<AbstractClientCard>();
        for (int i = 0; i < 12; i++) {
            model.addElement(new LocalCard(card.getCardColor(), i));
        }

        final var list = new CardList(CardList.Direction.SOUTH, model);
        final var l = new GridBagLayout();
        final var dialog = new JDialog(client.getMainWindow(), "数字を推理する", true);
        dialog.setLayout(l);

        final var attack = new JXButton("アタックする");
        attack.addActionListener(e -> {
            if (list.isSelectionEmpty() || client.getConnection() == null) {
                return;
            }

            final var guessed = (AbstractClientCard) list.getSelectedValue();
            this.onClientAction(new AttackActions.DoAttack(card.getId(), guessed.getNum()));
            dialog.dispose();
        });

        final var cancel = new JXButton("キャンセル");
        cancel.addActionListener(e -> dialog.dispose());

        addButton(dialog, new JXLabel("このカードは...", SwingConstants.CENTER), l, 0, 0, 2, 1, 1.0D, 0.05D);
        addButton(dialog, new JScrollPane(list), l, 0, 1, 2, 1, 1.0D);
        addButton(dialog, attack, l, 0, 2, 1, 1, 1.0D, 0.05D);
        addButton(dialog, cancel, l, 1, 2, 1, 1, 1.0D, 0.05D);

        dialog.pack();
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.setLocationRelativeTo(client.getMainWindow());
        dialog.setVisible(true);
    }

    @EventHandler
    public void onCardSelect(final CardSelectEvent event) {
        this.onClientAction(new AttackActions.Select(event.card().getId()));
    }

    @Override
    public void onExit(final ClientGame game, final GamePanel panel) {
        ClientGamePhase.super.onExit(game, panel);
        NumGuesser.getInstance().eventBus.unregister(this);
    }
}
