package com.hamusuke.numguesser.client.gui.component.panel.main.room;

import com.hamusuke.numguesser.client.gui.component.panel.Panel;
import com.hamusuke.numguesser.network.Player;
import com.hamusuke.numguesser.network.protocol.packet.serverbound.common.ReadyReq;
import org.jdesktop.swingx.JXButton;
import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Objects;

public class RoomPanel extends Panel {
    private JXButton ready;

    public RoomPanel() {
        super(new GridLayout(0, 1));
    }

    @Override
    public void init() {
        super.init();

        this.client.setWindowTitle(Objects.requireNonNull(this.client.curRoom).getRoomName() + " - " + this.client.getGameTitle());

        var label = new JXLabel("全員が準備完了になるとゲームが始まります", SwingConstants.CENTER);
        this.add(label);
        this.ready = new JXButton("準備完了");
        this.ready.addActionListener(this);
        this.add(this.ready);
    }

    public void hideReadyButton() {
        this.ready.setEnabled(false);
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
