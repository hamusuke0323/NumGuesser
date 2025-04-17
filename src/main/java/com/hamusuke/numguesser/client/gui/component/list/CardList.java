package com.hamusuke.numguesser.client.gui.component.list;

import com.hamusuke.numguesser.Constants;
import com.hamusuke.numguesser.client.game.card.AbstractClientCard;
import org.jdesktop.swingx.JXList;

import javax.swing.*;
import java.awt.*;

import static com.hamusuke.numguesser.Constants.CARD_HEIGHT;
import static com.hamusuke.numguesser.Constants.CARD_WIDTH;

public class CardList extends JXList {
    protected final Direction direction;

    public CardList(Direction direction, DefaultListModel<AbstractClientCard> listModel) {
        super(listModel);
        this.direction = direction;
        this.setLayoutOrientation(this.direction.layoutOrientation);
        this.setComponentOrientation(this.direction == Direction.NORTH ? ComponentOrientation.RIGHT_TO_LEFT : ComponentOrientation.LEFT_TO_RIGHT);

        if (this.direction == Direction.SOUTH || this.direction == Direction.NORTH) {
            this.setVisibleRowCount(1);
            this.setFixedCellWidth(CARD_WIDTH + 10);
            this.setFixedCellHeight(CARD_HEIGHT + 10);
        } else {
            this.setVisibleRowCount(Constants.ALL_CARD_NUM);
            this.setFixedCellWidth(CARD_HEIGHT + 10);
            this.setFixedCellHeight(CARD_WIDTH + 10);
        }

        this.setCellRenderer((list, value, index, isSelected, cellHasFocus) -> {
            var card = (AbstractClientCard) value;
            return card.toPanel(this.direction, isSelected, cellHasFocus);
        });
    }

    @Override
    public Object getElementAt(int viewIndex) {
        return this.direction != Direction.EAST ? super.getElementAt(viewIndex) : super.getElementAt(this.getModel().getSize() - 1 - viewIndex);
    }

    public Direction getDirection() {
        return this.direction;
    }

    public enum Direction {
        SOUTH(0.0D, JList.HORIZONTAL_WRAP, BorderLayout.SOUTH, new Dimension(CARD_WIDTH, CARD_HEIGHT)),
        EAST(Math.toRadians(-90), JList.VERTICAL_WRAP, BorderLayout.EAST, new Dimension(CARD_HEIGHT, CARD_WIDTH)),
        NORTH(Math.toRadians(180), JList.HORIZONTAL_WRAP, BorderLayout.NORTH, new Dimension(CARD_WIDTH, CARD_HEIGHT)),
        WEST(Math.toRadians(90), JList.VERTICAL_WRAP, BorderLayout.WEST, new Dimension(CARD_HEIGHT, CARD_WIDTH));

        public final double radToRotate;
        public final int layoutOrientation;
        public final String layoutDir;
        public final Dimension panelSize;

        Direction(double rad, int layoutOrientation, String layoutDir, Dimension panelSize) {
            this.radToRotate = rad;
            this.layoutOrientation = layoutOrientation;
            this.layoutDir = layoutDir;
            this.panelSize = panelSize;
        }

        public static Direction counterClockwiseFromSouth(int count) {
            return count >= 0 ? values()[count % 4] : clockwiseFromSouth(Math.abs(count));
        }

        public static Direction clockwiseFromSouth(int count) {
            return count >= 0 ? values()[(count * 3) % 4] : counterClockwiseFromSouth(Math.abs(count));
        }
    }
}
