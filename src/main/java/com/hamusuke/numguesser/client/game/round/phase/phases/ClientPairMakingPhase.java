package com.hamusuke.numguesser.client.game.round.phase.phases;

import com.hamusuke.numguesser.client.NumGuesser;
import com.hamusuke.numguesser.client.event.PairColorChangeEvent;
import com.hamusuke.numguesser.client.game.ClientGame;
import com.hamusuke.numguesser.client.game.round.phase.ClientActable;
import com.hamusuke.numguesser.client.game.round.phase.ClientGamePhase;
import com.hamusuke.numguesser.client.gui.component.list.PairList;
import com.hamusuke.numguesser.client.gui.component.panel.main.play.GamePanel;
import com.hamusuke.numguesser.event.EventHandler;
import com.hamusuke.numguesser.game.Game;
import com.hamusuke.numguesser.game.phase.action.actions.PairMakingActions;
import com.hamusuke.numguesser.game.phase.phases.PairMakingPhase;
import com.hamusuke.numguesser.util.Util;
import org.jdesktop.swingx.JXBusyLabel;
import org.jdesktop.swingx.JXButton;
import org.jdesktop.swingx.JXLabel;

import javax.swing.*;
import java.awt.*;

import static com.hamusuke.numguesser.client.gui.component.panel.Panel.addButton;

public class ClientPairMakingPhase extends PairMakingPhase implements ClientGamePhase, ClientActable<PairMakingActions> {
    @Override
    public void onEnter(final ClientGame game, final GamePanel panel) {
        final var client = NumGuesser.getInstance();
        final var center = panel.getCenterPanel();
        final var l = (GridBagLayout) center.getLayout();
        final var pairMap = game.getGameData(Game.PAIR_MAP);

        final var list = new PairList(client);
        list.addPairEntries(Util.transformToNewImmutableMapOnlyKeys(pairMap, game::getPlayer).keySet().stream().toList());

        final boolean owner = client.curRoom.amIOwner();
        final var label = new JXBusyLabel();
        label.setBusy(true);
        label.setText("部屋のオーナーがペアを決めています...");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        addButton(center, label, l, 0, 0, 1, 1, 0.0125D);

        if (owner) {
            addButton(center, new JXLabel("プレイヤーをクリックして2人1組を作ります", SwingConstants.CENTER), l, 0, 1, 1, 1, 0.0125D);

            var done = new JXButton("ゲームを始める");
            done.addActionListener(e -> this.onClientAction(PairMakingActions.PairMakingDone.INSTANCE));
            addButton(center, done, l, 0, 3, 1, 1, 0.025D);
        }

        client.eventBus.register(this);
        addButton(center, list, l, 0, 2, 1, 1, 1.0D);
    }

    @EventHandler
    public void onPairColorChange(final PairColorChangeEvent event) {
        this.onClientAction(new PairMakingActions.PairColorChange(event.player().getId(), event.color()));
    }

    @Override
    public void onExit(ClientGame game, GamePanel panel) {
        NumGuesser.getInstance().eventBus.unregister(this);
        ClientGamePhase.super.onExit(game, panel);
    }
}
