package com.hamusuke.numguesser.client.gui.component.table;

import com.google.common.collect.Lists;
import com.hamusuke.numguesser.client.game.SinglePlayerGame.Difficulty;
import com.hamusuke.numguesser.client.game.card.AbstractClientCard;
import com.hamusuke.numguesser.client.game.card.ClientFrameCard;
import com.hamusuke.numguesser.client.gui.component.list.CardList.Direction;
import com.hamusuke.numguesser.game.card.Card;

import javax.annotation.Nullable;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

import static com.hamusuke.numguesser.Constants.CARD_HEIGHT;

public class SinglePlayerGameTable extends JTable {
    public static final Object VOID_CARD = new Object();
    private final DefaultTableModel tableModel;
    private final Difficulty difficulty;

    public SinglePlayerGameTable(Difficulty difficulty) {
        this.difficulty = difficulty;
        this.setModel(this.tableModel = this.createTable());
        this.setDragEnabled(false);
        this.setColumnSelectionAllowed(false);
        this.setRowHeight(CARD_HEIGHT + 10);
    }

    private DefaultTableModel createTable() {
        var model = new DefaultTableModel(this.difficulty.firstOpenedCardNum, this.difficulty.columnSize);
        for (int row = 0; row < model.getRowCount(); row++) {
            for (int col = 0; col < model.getColumnCount(); col++) {
                model.setValueAt(new ClientFrameCard(), row, col);
            }
        }

        return model;
    }

    public void setCardTo(Card card, int row, int column) {
        if (this.isFullInRow(row)) {
            return;
        }

        this.tableModel.setValueAt(card, row, column);
        this.clearSelection();
        this.makeGapsBetweenCards(row, column);

        if (this.isFullInRow(row)) {
            this.completeRow(row);
        }

        SwingUtilities.invokeLater(this::repaint);
    }

    private void makeGapsBetweenCards(int row, int column) {
        if (this.isFullInRow(row) || column == this.difficulty.centerIndex) {
            return;
        }

        boolean leftSide = this.difficulty.centerIndex > column;
        if (leftSide) {
            // let cards through 0 ~ center - 1 move left.
            for (int i = 0; i < column; i++) {
                this.tableModel.setValueAt(this.tableModel.getValueAt(row, i + 1), row, i);
            }
        } else {
            // move right.
            for (int i = this.tableModel.getColumnCount() - 1; i > column; i--) {
                this.tableModel.setValueAt(this.tableModel.getValueAt(row, i - 1), row, i);
            }
        }

        this.tableModel.setValueAt(new ClientFrameCard(2), row, column);
    }

    private void completeRow(int row) {
        var cards = Lists.newArrayList(this.tableModel.getDataVector().get(row));
        var left = Lists.newArrayList(cards.subList(0, this.difficulty.centerIndex));
        var right = Lists.newArrayList(cards.subList(this.difficulty.centerIndex + 1, cards.size()));
        left.removeIf(o -> o instanceof ClientFrameCard);
        right.removeIf(o -> o instanceof ClientFrameCard);

        int putCardNum = 0;
        for (int i = this.difficulty.centerIndex - 1; i >= 0; i--) {
            if (putCardNum++ < left.size()) {
                this.tableModel.setValueAt(left.get(left.size() - 1 - (putCardNum - 1)), row, i);
                continue;
            }

            this.tableModel.setValueAt(VOID_CARD, row, i);
        }

        putCardNum = 0;
        for (int i = this.difficulty.centerIndex + 1; i < this.difficulty.columnSize; i++) {
            if (putCardNum++ < right.size()) {
                this.tableModel.setValueAt(right.get(putCardNum - 1), row, i);
                continue;
            }

            this.tableModel.setValueAt(VOID_CARD, row, i);
        }
    }

    private boolean isFullInRow(int row) {
        return this.tableModel.getDataVector().get(row).stream().filter(o -> !(o instanceof ClientFrameCard)).count() >= this.difficulty.maxRowCardNum;
    }

    public boolean isValidPos(int row, int column) {
        if (this.isFullInRow(row)) {
            return false;
        }

        boolean leftCardExists = column > 0 && !(this.tableModel.getValueAt(row, column - 1) instanceof ClientFrameCard);
        boolean rightCardExists = column < this.tableModel.getColumnCount() - 1 && !(this.tableModel.getValueAt(row, column + 1) instanceof ClientFrameCard);

        return (leftCardExists || rightCardExists) && this.tableModel.getValueAt(row, column) instanceof ClientFrameCard;
    }

    @Override
    public TableCellRenderer getCellRenderer(int row, int column) {
        return (table, value, isSelected, hasFocus, row1, column1) -> {
            if (value instanceof AbstractClientCard card) {
                return card.toPanel(Direction.SOUTH, isSelected, hasFocus);
            }

            return null;
        };
    }

    @Nullable
    @Override
    public JTableHeader getTableHeader() {
        return null;
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }
}
