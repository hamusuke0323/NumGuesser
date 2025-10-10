package com.hamusuke.numguesser.client.gui.component.table;

import com.hamusuke.numguesser.network.PacketLogger.PacketDetails;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import com.hamusuke.numguesser.util.PacketUtil;
import com.hamusuke.numguesser.util.Util;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class PacketInfoRenderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (!(value instanceof PacketDetails(Packet<?> packet, int size))) {
            if (column == 0) {
                var l = new JLabel("←");
                l.setHorizontalAlignment(RIGHT);
                return l;
            }

            return new JLabel("→");
        }

        var byteStr = "(%s)".formatted(PacketUtil.convertBytes(size));
        var l = new JLabel(packet.getClass().getSimpleName() + byteStr);
        var packetDetails = PacketUtil.getPacketDetails(packet, byteStr);
        l.setToolTipText(Util.toHTML(packetDetails));

        return l;
    }
}
