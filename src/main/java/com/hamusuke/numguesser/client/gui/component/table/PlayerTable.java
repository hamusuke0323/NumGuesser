package com.hamusuke.numguesser.client.gui.component.table;

import com.hamusuke.numguesser.client.NumGuesser;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

public class PlayerTable extends JTable {
    private static final String PLAYER_COLUMN = "プレイヤー";
    private static final String POINT_COLUMN = "得点";
    private static final DefaultTableModel MODEL = new DefaultTableModel(new String[]{PLAYER_COLUMN}, 0);
    protected final NumGuesser client;

    public PlayerTable(NumGuesser client) {
        super(MODEL);
        this.client = client;
        this.setDragEnabled(false);
        this.setColumnSelectionAllowed(false);
        this.setCellSelectionEnabled(false);
        this.setRowHeight(30);
    }

    @Override
    public TableCellRenderer getCellRenderer(int row, int column) {
        return this.getColumnName(column).equals(PLAYER_COLUMN) ? new PlayerInfoRenderer() : super.getCellRenderer(row, column);
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    public void addPointColumn() {
        MODEL.addColumn(POINT_COLUMN);
    }

    public void removePointColumn() {
        MODEL.setColumnCount(1);
    }

    public void update() {
        this.clear();
        if (this.client.curRoom == null) {
            return;
        }

        synchronized (this.client.curRoom.getPlayers()) {
            this.client.curRoom.getPlayers().forEach(player -> MODEL.addRow(new Object[]{player, player.getTipPoint()}));
        }
    }

    public void clear() {
        MODEL.getDataVector().clear();
        MODEL.setRowCount(0);
    }
}
