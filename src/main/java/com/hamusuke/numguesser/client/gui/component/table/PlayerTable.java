package com.hamusuke.numguesser.client.gui.component.table;

import com.hamusuke.numguesser.client.NumGuesser;

import javax.annotation.Nullable;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.util.function.Consumer;

public class PlayerTable extends JTable {
    private static final DefaultTableModel MODEL = new DefaultTableModel(new String[]{"プレイヤー"}, 0);
    protected final NumGuesser client;
    @Nullable
    protected Consumer<DefaultTableModel> overrideRenderer;

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
        return this.getColumnName(column).equals("プレイヤー") ? new PlayerInfoRenderer() : super.getCellRenderer(row, column);
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    public void setOverrideRenderer(@Nullable Consumer<DefaultTableModel> overrideRenderer) {
        this.overrideRenderer = overrideRenderer;
    }

    public void update() {
        this.clear();
        if (this.client.curRoom == null) {
            return;
        }

        if (this.overrideRenderer != null) {
            this.overrideRenderer.accept(MODEL);
            return;
        }

        synchronized (this.client.curRoom.getPlayers()) {
            this.client.curRoom.getPlayers().forEach(player -> MODEL.addRow(new Object[]{player}));
        }
    }

    public void clear() {
        MODEL.getDataVector().clear();
        MODEL.setRowCount(0);
    }
}
