package com.hamusuke.numguesser.client.gui;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.extras.FlatAnimatedLafChange;
import com.hamusuke.numguesser.Constants;
import com.hamusuke.numguesser.client.NumGuesser;
import com.hamusuke.numguesser.client.gui.component.panel.Panel;
import com.hamusuke.numguesser.client.gui.component.panel.dialog.CenteredMessagePanel;
import com.hamusuke.numguesser.network.protocol.packet.common.serverbound.LeaveRoomReq;
import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MainWindow extends JXFrame implements ActionListener, WindowListener, ComponentListener {
    private final NumGuesser client;
    private JMenuBar curMenuBar;
    private Panel curPanel;
    private JXPanel south;
    public final JMenuItem packetLog;
    public final JCheckBoxMenuItem autoScroll;
    public final JScrollPane logScroll;
    public final JToggleButton themeToggle;

    public MainWindow(NumGuesser client) {
        super(Constants.TITLE_AND_VERSION);
        this.client = client;
        this.addWindowListener(this);
        this.addComponentListener(this);

        this.logScroll = new JScrollPane(this.client.packetLogTable);

        this.autoScroll = new JCheckBoxMenuItem("オートスクロール");
        this.autoScroll.setState(true);
        this.autoScroll.addActionListener(e -> this.onPacketLog());

        this.packetLog = new JMenuItem("ログを見る");
        this.packetLog.addActionListener(this);
        this.packetLog.setActionCommand("packetLog");

        this.themeToggle = new JToggleButton("ダークテーマ");
        this.themeToggle.addActionListener(e -> EventQueue.invokeLater(() -> {
            FlatAnimatedLafChange.showSnapshot();
            if (FlatLaf.isLafDark()) {
                FlatLightLaf.setup();
                this.onThemeChanged(false);
            } else {
                FlatDarkLaf.setup();
                this.onThemeChanged(true);
            }

            this.updateUI();
            FlatAnimatedLafChange.hideSnapshotWithAnimation();
        }));
    }

    public void tick() {
        this.curPanel.tick();
    }

    private void onThemeChanged(boolean dark) {
        this.client.sendMsg(() -> {
            this.client.config.darkTheme.setValue(dark);
            this.client.config.save();
        });
    }

    public void updateUI() {
        FlatLaf.updateUI();
        this.packetLog.updateUI();
        this.autoScroll.updateUI();
        this.logScroll.updateUI();
        this.client.packetLogTable.updateUI();
    }

    public void onPacketLog() {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(this::onPacketLog);
            return;
        }

        if (!this.autoScroll.isSelected()) {
            return;
        }

        this.logScroll.getVerticalScrollBar().setValue(this.logScroll.getVerticalScrollBar().getMaximum());
        this.logScroll.repaint();
    }

    public void setPanel(Panel panel) {
        if (this.curPanel != null) {
            this.curPanel.onRemoved();
            this.remove(this.curPanel);
        }

        boolean je = this.curPanel == panel;
        this.curPanel = panel;
        this.curPanel.init();
        this.getContentPane().add(this.curPanel, BorderLayout.CENTER);

        if (!je) {
            this.updateMenuBar();
            this.updateSouth();
        }

        this.revalidate();
        this.repaint();
    }

    public Panel getPanel() {
        return this.curPanel;
    }

    private void updateMenuBar() {
        var bar = this.curPanel.createMenuBar();
        if (bar == null) {
            return;
        }

        this.removeMenuBar();
        this.curMenuBar = bar;
        this.getContentPane().add(this.curMenuBar, BorderLayout.NORTH);
    }

    private void removeMenuBar() {
        if (this.curMenuBar != null) {
            this.remove(this.curMenuBar);
        }
    }

    private void updateSouth() {
        var s = this.curPanel.createSouth();
        if (s == null) {
            return;
        }

        this.removeSouth();
        this.south = s;
        this.south.setPreferredSize(new Dimension(100, this.getHeight() / 4));
        this.getContentPane().add(this.south, BorderLayout.SOUTH);
    }

    private void removeSouth() {
        if (this.south != null) {
            this.remove(this.south);
        }
    }

    private synchronized void showPacketLog() {
        this.add(this.logScroll, BorderLayout.EAST);
        this.packetLog.setText("ログを閉じる");
        this.packetLog.setActionCommand("hPacketLog");
        this.revalidate();
    }

    private synchronized void hidePacketLog() {
        this.remove(this.logScroll);
        this.packetLog.setText("ログを見る");
        this.packetLog.setActionCommand("packetLog");
        this.revalidate();
    }

    private void clearChat() {
        if (this.client.chat != null) {
            this.client.chat.clear();
        }
    }

    private void clearPacketLogs() {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(this::clearPacketLogs);
            return;
        }

        this.client.packetLogTable.clear();
        this.logScroll.getVerticalScrollBar().setValue(0);
        this.revalidate();
        this.repaint();
    }

    private void leaveRoom() {
        this.reset(false);
        this.client.setPanel(new CenteredMessagePanel("部屋を出ています..."));
        this.client.getConnection().sendPacket(LeaveRoomReq.INSTANCE);
    }

    public void reset() {
        this.reset(true);
    }

    public void reset(boolean hidePacketLog) {
        this.removeMenuBar();
        this.removeSouth();
        if (hidePacketLog) {
            this.hidePacketLog();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "disconnect":
                this.client.disconnect();
                break;
            case "clearChat":
                this.clearChat();
                break;
            case "packetLog":
                this.showPacketLog();
                break;
            case "hPacketLog":
                this.hidePacketLog();
                break;
            case "clearPackets":
                this.clearPacketLogs();
                break;
            case "leave":
                this.leaveRoom();
                break;
            default:
                this.curPanel.actionPerformed(e);
        }
    }

    @Override
    public void componentResized(ComponentEvent e) {
        this.curPanel.componentResized(e);

        var c = e.getComponent();
        if (c != this) {
            return;
        }

        if (this.south != null) {
            this.south.setPreferredSize(new Dimension(100, c.getHeight() / 4));
            this.south.revalidate();
        }

        var logSize = new Dimension(c.getWidth() / 4, 100);
        this.logScroll.setPreferredSize(logSize);

        if (this.autoScroll.getState()) {
            SwingUtilities.invokeLater(() -> {
                this.logScroll.getVerticalScrollBar().setValue(this.logScroll.getVerticalScrollBar().getMaximum());
            });
        }
    }

    @Override
    public void componentMoved(ComponentEvent e) {
    }

    @Override
    public void componentShown(ComponentEvent e) {
    }

    @Override
    public void componentHidden(ComponentEvent e) {
    }

    @Override
    public void windowOpened(WindowEvent e) {
    }

    @Override
    public void windowClosing(WindowEvent e) {
        this.client.stopLooping();
    }

    @Override
    public void windowClosed(WindowEvent e) {
    }

    @Override
    public void windowIconified(WindowEvent e) {
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
    }

    @Override
    public void windowActivated(WindowEvent e) {
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
    }
}
