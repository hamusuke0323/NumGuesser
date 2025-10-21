package com.hamusuke.numguesser.client.gui.component.panel.lobby;

import com.hamusuke.numguesser.Constants;
import com.hamusuke.numguesser.client.gui.component.panel.Panel;
import com.hamusuke.numguesser.client.gui.component.panel.dialog.CenteredMessagePanel;
import com.hamusuke.numguesser.client.gui.component.panel.dialog.NewRoomPanel;
import com.hamusuke.numguesser.network.protocol.packet.lobby.serverbound.CreateRoomReq;
import com.hamusuke.numguesser.network.protocol.packet.lobby.serverbound.JoinRoomReq;
import com.hamusuke.numguesser.network.protocol.packet.lobby.serverbound.RoomListQueryReq;
import com.hamusuke.numguesser.network.protocol.packet.lobby.serverbound.RoomListReq;
import com.hamusuke.numguesser.room.Room;
import com.hamusuke.numguesser.room.RoomInfo;
import org.jdesktop.swingx.JXButton;
import org.jdesktop.swingx.JXList;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTextField;

import javax.annotation.Nullable;
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Optional;

public class LobbyPanel extends Panel implements ListSelectionListener {
    private JXList list;
    private JXTextField searchBox;
    private JXButton search;
    private JXButton join;
    private JXButton refresh;
    private int refreshTicks;

    @Override
    public void init() {
        super.init();

        this.client.setWindowTitle("ロビー - " + Constants.TITLE_AND_VERSION);
        var sl = new GridBagLayout();
        var searchPanel = new JXPanel(sl);
        this.searchBox = new JXTextField();
        this.searchBox.addActionListener(e -> {
            this.search();
        });
        this.searchBox.setToolTipText("検索");
        this.search = new JXButton("検索");
        this.search.setActionCommand("search");
        this.search.addActionListener(this);
        addButton(searchPanel, this.searchBox, sl, 0, 0, 1, 1, 0.125D);
        addButton(searchPanel, this.search, sl, 1, 0, 1, 1, 0.2D, 0.125D);
        this.add(searchPanel, BorderLayout.NORTH);

        var model = new DefaultListModel<RoomInfo>();
        this.list = new JXList(model);
        this.list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.list.addListSelectionListener(this);
        this.list.setOpaque(true);
        this.list.setCellRenderer((list, value, index, isSelected, cellHasFocus) -> {
            if (!(value instanceof RoomInfo room)) {
                return new JXPanel();
            }

            var p = room.toPanel(isSelected ? this.list.getSelectionForeground() : this.list.getForeground());
            p.setBackground(isSelected ? this.list.getSelectionBackground() : this.list.getBackground());
            p.setBorder(new LineBorder(isSelected ? this.list.getSelectionForeground() : this.list.getSelectionBackground(), 1));
            return p;
        });
        this.add(new JScrollPane(this.list), BorderLayout.CENTER);

        var l = new GridBagLayout();
        var south = new JXPanel(l);

        this.join = new JXButton("参加する");
        this.join.setEnabled(false);
        this.join.setActionCommand("join");
        this.join.addActionListener(this);

        var add = new JXButton("部屋を作る");
        add.setActionCommand("create");
        add.addActionListener(this);

        this.refresh = new JXButton("更新");
        this.refresh.setActionCommand("refresh");
        this.refresh.addActionListener(this);

        addButton(south, this.join, l, 0, 0, 1, 1, 0.125D);
        addButton(south, this.refresh, l, 0, 1, 1, 1, 0.125D);
        addButton(south, add, l, 0, 4, 1, 1, 0.125D);

        this.add(south, BorderLayout.SOUTH);
        this.refresh();
    }

    @Override
    public void tick() {
        if (this.refreshTicks > 0) {
            this.refreshTicks--;
            if (this.refreshTicks <= 0) {
                this.refresh.setEnabled(true);
            }
        }
    }

    @Nullable
    @Override
    public JMenuBar createMenuBar() {
        var bar = new JMenuBar();
        bar.add(this.createMenuMenu());
        bar.add(this.createNetworkMenu());
        bar.add(this.createThemeMenu());
        return bar;
    }

    private Optional<RoomInfo> getSelectionOrDialog() {
        var selection = this.list.getSelectedValue();
        if (selection == null) {
            return Optional.empty();
        }

        return Optional.of((RoomInfo) selection);
    }

    private void createRoom() {
        this.client.setPanel(new NewRoomPanel(p -> {
            if (!p.isAccepted()) {
                this.client.setPanel(this);
                return;
            }

            this.client.setPanel(new CenteredMessagePanel("部屋を作成しています..."));
            this.client.getConnection().sendPacket(new CreateRoomReq(p.getRoomName(), p.hasPassword() ? p.getPassword() : ""));
        }));
    }

    private void joinRoom() {
        this.getSelectionOrDialog().ifPresent(roomInfo -> {
            this.client.getConnection().sendPacket(new JoinRoomReq(roomInfo.id()));
        });
    }

    private void search() {
        var query = this.searchBox.getText();
        this.searchBox.setText("");
        if (query.isEmpty()) {
            return;
        }

        this.getModel().clear();
        this.onRoomListChanged();
        this.search.setEnabled(false);
        this.join.setEnabled(false);
        this.search.setText("検索中...");
        this.client.getConnection().sendPacket(new RoomListQueryReq(query.substring(0, Math.min(query.length(), Room.MAX_ROOM_NAME_LENGTH))));
    }

    private void refresh() {
        this.getModel().clear();
        this.onRoomListChanged();
        this.client.getConnection().sendPacket(RoomListReq.INSTANCE);
        this.join.setEnabled(false);
        this.refresh.setEnabled(false);
        this.refreshTicks = 60;
    }

    public void addAll(List<RoomInfo> infoList) {
        this.getModel().addAll(infoList);
        this.onRoomListChanged();
    }

    private DefaultListModel<RoomInfo> getModel() {
        return (DefaultListModel<RoomInfo>) this.list.getModel();
    }

    public void onRoomListChanged() {
        if (!this.search.isEnabled()) {
            this.search.setEnabled(true);
            this.search.setText("検索");
        }

        this.revalidate();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "create":
                this.createRoom();
                break;
            case "join":
                this.joinRoom();
                break;
            case "search":
                this.search();
                break;
            case "refresh":
                this.refresh();
                break;
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        this.join.setEnabled(!this.list.isSelectionEmpty());
    }
}
