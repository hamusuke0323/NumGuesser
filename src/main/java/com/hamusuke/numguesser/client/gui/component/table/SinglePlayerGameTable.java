package com.hamusuke.numguesser.client.gui.component.table;

import com.hamusuke.numguesser.client.game.card.AbstractClientCard;
import com.hamusuke.numguesser.client.game.card.ClientTransparentCard;
import com.hamusuke.numguesser.client.gui.component.list.CardList.Direction;
import com.hamusuke.numguesser.game.card.Card;
import com.hamusuke.numguesser.game.single.SinglePlayerGame.Difficulty;

import javax.annotation.Nullable;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

import static com.hamusuke.numguesser.Constants.CARD_HEIGHT;

public class SinglePlayerGameTable extends JTable {
    private final DefaultTableModel tableModel;

    public SinglePlayerGameTable(Difficulty difficulty) {
        this.setModel(this.tableModel = this.createTable(difficulty));
        this.setDragEnabled(false);
        this.setColumnSelectionAllowed(false);
        this.setRowHeight(CARD_HEIGHT + 10);
    }

    private DefaultTableModel createTable(Difficulty difficulty) {
        var model = new DefaultTableModel(difficulty.firstOpenedCardNum, difficulty.maxRowCardNum * 2 - 1);
        for (int row = 0; row < model.getRowCount(); row++) {
            for (int col = 0; col < model.getColumnCount(); col++) {
                model.setValueAt(new ClientTransparentCard(), row, col);
            }
        }

        return model;
    }

    public void setCardTo(Card card, int row, int column) {
        this.tableModel.setValueAt(card, row, column);
    }

    @Override
    public TableCellRenderer getCellRenderer(int row, int column) {
        return (table, value, isSelected, hasFocus, row1, column1) -> {
            if (value instanceof AbstractClientCard card) {
                return card.toPanel(Direction.SOUTH, isSelected, hasFocus);
            }

            return new JPanel();
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
