package com.hamusuke.numguesser.client.gui.component.table;

import com.hamusuke.numguesser.client.network.player.AbstractClientPlayer;
import org.jdesktop.swingx.JXLabel;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class PlayerInfoRenderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (value instanceof AbstractClientPlayer clientPlayer) {
            var label = new JXLabel(clientPlayer.getName(), CENTER);
            label.setToolTipText(String.format("id: %s, ping: %dms", clientPlayer.getId(), clientPlayer.getPing()));
            return label;
        }

        return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    }
}
