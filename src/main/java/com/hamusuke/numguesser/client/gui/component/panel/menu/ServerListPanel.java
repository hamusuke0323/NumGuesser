package com.hamusuke.numguesser.client.gui.component.panel.menu;

import com.hamusuke.numguesser.Constants;
import com.hamusuke.numguesser.client.gui.component.panel.Panel;
import com.hamusuke.numguesser.client.gui.component.panel.dialog.ConfirmPanel;
import com.hamusuke.numguesser.client.gui.component.panel.dialog.ConnectingPanel;
import com.hamusuke.numguesser.client.gui.component.panel.dialog.ServerInfoPanel;
import com.hamusuke.numguesser.client.network.ServerInfoRegistry;
import com.hamusuke.numguesser.network.ServerInfo;
import com.hamusuke.numguesser.network.ServerInfo.Status;
import org.jdesktop.swingx.JXButton;
import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXList;
import org.jdesktop.swingx.JXPanel;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Optional;

public class ServerListPanel extends Panel implements ListSelectionListener {
    private final ServerInfoRegistry serverInfoRegistry = this.client.getServerInfoRegistry();
    private JXList list;
    private JXButton connect;
    private JXButton remove;
    private JXButton edit;
    private JXButton refresh;
    private int refreshBtnTicks;

    @Override
    public void init() {
        super.init();

        this.client.setWindowTitle("マルチプレイ - " + Constants.TITLE_AND_VERSION);
        var model = new DefaultListModel<ServerInfo>();
        model.addAll(this.serverInfoRegistry.getServers());
        this.list = new JXList(model);
        this.list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.list.addListSelectionListener(this);
        this.list.setOpaque(true);
        this.list.setCellRenderer((list, value, index, isSelected, cellHasFocus) -> {
            var label = new JXLabel(value.toString());
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setForeground(isSelected ? this.list.getSelectionForeground() : this.list.getForeground());
            var panel = new JXPanel();
            panel.setBackground(isSelected ? this.list.getSelectionBackground() : this.list.getBackground());
            panel.setBorder(new LineBorder(isSelected ? this.list.getSelectionForeground() : this.list.getSelectionBackground(), 1));
            panel.add(label);
            return panel;
        });
        this.add(new JScrollPane(this.list), BorderLayout.CENTER);

        var l = new GridBagLayout();
        var south = new JPanel(l);

        this.connect = new JXButton("接続");
        this.connect.setEnabled(false);
        this.connect.setActionCommand("con");
        this.connect.addActionListener(this);

        this.remove = new JXButton("削除");
        this.remove.setEnabled(false);
        this.remove.setActionCommand("rm");
        this.remove.addActionListener(this);

        var add = new JXButton("サーバーを追加");
        add.setActionCommand("add");
        add.addActionListener(this);

        this.edit = new JXButton("編集");
        this.edit.setEnabled(false);
        this.edit.setActionCommand("edit");
        this.edit.addActionListener(this);

        this.refresh = new JXButton("更新");
        this.refresh.setEnabled(false);
        this.refresh.setActionCommand("refresh");
        this.refresh.addActionListener(this);

        var back = new JXButton("戻る");
        back.setActionCommand("back");
        back.addActionListener(this);

        addButton(south, this.connect, l, 0, 0, 1, 1, 0.125D);
        addButton(south, this.refresh, l, 0, 1, 1, 1, 0.125D);
        addButton(south, this.edit, l, 0, 2, 1, 1, 0.125D);
        addButton(south, this.remove, l, 0, 3, 1, 1, 0.125D);
        addButton(south, add, l, 0, 4, 1, 1, 0.125D);
        addButton(south, back, l, 0, 5, 1, 1, 0.125D);

        this.add(south, BorderLayout.SOUTH);
        this.setSize(new Dimension(this.getWidth() * 2, this.getHeight()));
    }

    @Override
    public void tick() {
        if (this.refreshBtnTicks > 0) {
            this.refreshBtnTicks--;
            if (this.refreshBtnTicks <= 0) {
                this.refresh.setEnabled(true);
            }
        }
    }

    private void connect() {
        this.getSelectionOrDialog().ifPresent(info -> this.client.setPanel(new ConnectingPanel(info.address, info.port)));
    }

    private void remove() {
        this.getSelectionOrDialog().ifPresent(info -> this.client.setPanel(new ConfirmPanel(this, "削除してもよろしいですか", b -> {
            if (b) {
                this.serverInfoRegistry.remove(info);
                this.serverInfoRegistry.save();
                ((DefaultListModel<?>) this.list.getModel()).removeElement(info);
            }
        })));
    }

    private void add() {
        this.client.setPanel(new ServerInfoPanel(this, d -> {
            var info = new ServerInfo(d.getAddress(), d.getPort());
            if (this.serverInfoRegistry.add(info)) {
                this.serverInfoRegistry.save();
                ((DefaultListModel<ServerInfo>) this.list.getModel()).addElement(info);
            }
        }));
    }

    private void edit() {
        this.getSelectionOrDialog().ifPresent(info -> this.client.setPanel(new ServerInfoPanel(this, d -> {
            var newInfo = new ServerInfo(d.getAddress(), d.getPort());
            if (!info.equals(newInfo) && this.serverInfoRegistry.contains(newInfo)) {
                this.serverInfoRegistry.remove(info);
                ((DefaultListModel<ServerInfo>) this.list.getModel()).removeElement(info);
            } else {
                info.address = newInfo.address;
                info.port = newInfo.port;
            }

            this.serverInfoRegistry.save();
            this.list.repaint();
        }, info)));
    }

    private Optional<ServerInfo> getSelectionOrDialog() {
        var selection = this.list.getSelectedValue();
        if (selection == null) {
            return Optional.empty();
        }

        return Optional.of((ServerInfo) selection);
    }

    private void refresh() {
        this.getSelectionOrDialog().ifPresent(info -> {
            if (info.status == Status.CONNECTING) {
                return;
            }

            this.client.getServerChecker().startChecking(info);
            this.refresh.setEnabled(false);
            this.refreshBtnTicks = 40;
        });
    }

    public void onServerInfoChanged() {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(this::onServerInfoChanged);
            return;
        }

        this.list.repaint();
        this.repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "con":
                this.connect();
                break;
            case "rm":
                this.remove();
                break;
            case "add":
                this.add();
                break;
            case "edit":
                this.edit();
                break;
            case "refresh":
                this.refresh();
                break;
            case "back":
                this.client.setPanel(new MainMenuPanel());
                break;
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        this.connect.setEnabled(true);
        this.remove.setEnabled(true);
        this.edit.setEnabled(true);
        this.refresh.setEnabled(true);
    }
}
