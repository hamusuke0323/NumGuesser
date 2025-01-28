package com.hamusuke.numguesser.client.gui.component.list;

import com.hamusuke.numguesser.client.NumGuesser;
import com.hamusuke.numguesser.client.network.player.AbstractClientPlayer;
import com.hamusuke.numguesser.network.protocol.packet.play.serverbound.PairColorChangeReq;
import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXList;
import org.jdesktop.swingx.JXPanel;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class PairList extends JXList {
    private final DefaultListModel<AbstractClientPlayer> model = new DefaultListModel<>();
    private final NumGuesser client;

    public PairList(NumGuesser client) {
        this.setModel(this.model);
        this.client = client;
        this.setCellRenderer((list, value, index, isSelected, cellHasFocus) -> {
            if (value instanceof AbstractClientPlayer player) {
                var p = new JXPanel();
                var l = new JXLabel(player.getName());
                l.setForeground(player.getPairColor().color);
                p.add(l);
                return p;
            }

            return null;
        });
        this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int i = locationToIndex(e.getPoint());
                if (i != -1) {
                    onSelected(i);
                }
            }
        });
    }

    public void addPairEntries(List<AbstractClientPlayer> entries) {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(() -> this.addPairEntries(entries));
            return;
        }

        this.model.addAll(entries);
    }

    public void clear() {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(this::clear);
            return;
        }

        this.model.clear();
    }

    private void onSelected(int index) {
        if (this.client.curRoom == null || !this.client.curRoom.amIOwner()) {
            return;
        }

        var e = this.model.get(index);
        if (e != null && this.client.getConnection() != null) {
            e.setPairColor(e.getPairColor().opposite());
            this.client.getConnection().sendPacket(new PairColorChangeReq(e.getId(), e.getPairColor()));
        }
    }
}
