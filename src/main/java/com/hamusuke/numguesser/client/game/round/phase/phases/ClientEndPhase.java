package com.hamusuke.numguesser.client.game.round.phase.phases;

import com.hamusuke.numguesser.client.NumGuesser;
import com.hamusuke.numguesser.client.game.ClientGame;
import com.hamusuke.numguesser.client.game.round.phase.ClientActable;
import com.hamusuke.numguesser.client.game.round.phase.ClientGamePhase;
import com.hamusuke.numguesser.client.gui.component.panel.main.play.GamePanel;
import com.hamusuke.numguesser.game.Game;
import com.hamusuke.numguesser.game.data.DataListener;
import com.hamusuke.numguesser.game.data.GameDataSyncer;
import com.hamusuke.numguesser.game.phase.action.actions.ButtonPressAction;
import com.hamusuke.numguesser.game.phase.phases.EndPhase;
import com.hamusuke.numguesser.network.protocol.packet.play.serverbound.ClientCommandReq;
import org.jdesktop.swingx.JXButton;
import org.jdesktop.swingx.JXLabel;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.util.List;

import static com.hamusuke.numguesser.client.gui.component.panel.Panel.addButton;

public class ClientEndPhase extends EndPhase implements ClientGamePhase, ClientActable<ButtonPressAction>, DataListener {
    @Nullable
    private JXButton readyButton;

    @Override
    public void onEnter(final ClientGame game, final GamePanel panel) {
        final boolean lastRound = game.getGameData(Game.LAST_ROUND);
        final var center = panel.getCenterPanel();
        final var layout = (GridBagLayout) center.getLayout();
        final var client = NumGuesser.getInstance();

        addButton(center, new JXLabel(lastRound ? "全てのラウンドが終了しました" : "ラウンド終了 全員が準備完了になると次のラウンドを開始します", SwingConstants.CENTER), layout, 0, 0, 1, 1, 1.0D, 0.05D);

        JXButton button;
        if (lastRound) {
            button = new JXButton("戻る");
            button.addActionListener(e -> client.executeSync(() -> {
                if (client.getConnection() == null) {
                    return;
                }

                client.getConnection().sendPacket(new ClientCommandReq(ClientCommandReq.Command.EXIT_GAME));
            }));
        } else {
            button = this.readyButton = new JXButton("準備完了");
            button.addActionListener(e -> this.onClientAction(ButtonPressAction.INSTANCE));
        }

        addButton(center, button, layout, 0, 1, 1, 1, 1.0D, 0.05D);
    }

    @Override
    public <V> void onDataChanged(final GameDataSyncer.Entry<V> data) {
        final var client = NumGuesser.getInstance();
        if (data.getData() == Game.READY_PLAYERS) {
            final var ids = (List<Integer>) data.getValue();
            if (ids.contains(client.clientPlayer.getId())) {
                this.readyButton.setEnabled(false);
            }

            this.readyButton.setText("準備完了（%d / %d）".formatted(ids.size(), client.curRoom.getPlayers().size()));
        }
    }
}
