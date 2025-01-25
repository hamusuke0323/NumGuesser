package com.hamusuke.numguesser.client.gui.component.panel.main.room;

import com.hamusuke.numguesser.client.gui.component.panel.Panel;
import com.hamusuke.numguesser.game.GameMode;
import com.hamusuke.numguesser.network.Player;
import com.hamusuke.numguesser.network.protocol.packet.serverbound.common.GameModeSelectReq;
import com.hamusuke.numguesser.network.protocol.packet.serverbound.common.ReadyReq;
import org.jdesktop.swingx.JXButton;
import org.jdesktop.swingx.JXComboBox;
import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.util.Objects;

public class RoomPanel extends Panel {
    private JXComboBox modeBox;
    private JXLabel modeLabel;
    private JXButton ready;

    public RoomPanel() {
        super(new GridLayout(0, 1));
    }

    @Override
    public void init() {
        super.init();

        this.client.setWindowTitle(Objects.requireNonNull(this.client.curRoom).getRoomName() + " - " + this.client.getGameTitleWithVersion());

        var label = new JXLabel("全員が準備完了になるとゲームが始まります", SwingConstants.CENTER);
        this.add(label);

        this.add(new JXLabel("ゲームモード", SwingConstants.CENTER));

        this.modeBox = new JXComboBox(GameMode.values());
        this.modeBox.addItemListener(e -> {
            if (this.client.curRoom.amIOwner() && e.getStateChange() == ItemEvent.SELECTED && this.client.getConnection() != null) {
                this.client.getConnection().sendPacket(new GameModeSelectReq((GameMode) e.getItem()));
            }
        });
        this.add(this.modeBox);

        this.modeLabel = new JXLabel(this.client.curRoom.getGameMode().name, SwingConstants.CENTER);
        this.add(this.modeLabel);

        this.setVisibleOfComponents();

        this.ready = new JXButton("準備完了");
        this.ready.addActionListener(this);
        this.add(this.ready);
    }

    private void setVisibleOfComponents() {
        if (Objects.requireNonNull(this.client.curRoom).amIOwner()) {
            this.modeBox.setVisible(true);
            this.modeLabel.setVisible(false);
        } else {
            this.modeBox.setVisible(false);
            this.modeLabel.setVisible(true);
        }

        SwingUtilities.invokeLater(() -> {
            this.revalidate();
            this.repaint();
        });
    }

    public void onOwnerChanged() {
        this.setVisibleOfComponents();
    }

    public void onGameModeChanged() {
        var mode = Objects.requireNonNull(this.client.curRoom).getGameMode();
        this.modeBox.setSelectedItem(mode);
        this.modeLabel.setText(mode.name);

        SwingUtilities.invokeLater(this::repaint);
    }

    public void hideReadyButton() {
        this.ready.setEnabled(false);
    }

    public void reviveReadyButton() {
        this.ready.setEnabled(true);
    }

    public void countReadyPlayers() {
        synchronized (this.client.curRoom.getPlayers()) {
            var players = this.client.curRoom.getPlayers();
            int readyPlayers = (int) players.stream().filter(Player::isReady).count();
            this.ready.setText("準備完了（%d / %d）".formatted(readyPlayers, players.size()));
        }
    }

    @Override
    public JMenuBar createMenuBar() {
        var jMenuBar = new JMenuBar();
        jMenuBar.add(this.createMenuMenu());
        jMenuBar.add(this.createChatMenu());
        jMenuBar.add(this.createNetworkMenu());
        jMenuBar.add(this.createThemeMenu());
        return jMenuBar;
    }

    @Override
    protected JMenu createMenuMenu() {
        var m = super.createMenuMenu();
        var leave = new JMenuItem("部屋から退出");
        leave.setActionCommand("leave");
        leave.addActionListener(this.client.getMainWindow());
        m.insert(leave, 0);
        return m;
    }

    @Override
    public JXPanel createSouth() {
        var layout = new GridBagLayout();
        var south = new JXPanel(layout);
        var chatPanel = this.createChatPanel();
        var table = this.createPlayerTable();
        addButton(south, chatPanel, layout, 0, 0, 2, 1, 1.0D);
        addButton(south, table, layout, 2, 0, 1, 1, 0.5D, 1.0D);
        return south;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        this.client.getConnection().sendPacket(new ReadyReq());
    }
}
