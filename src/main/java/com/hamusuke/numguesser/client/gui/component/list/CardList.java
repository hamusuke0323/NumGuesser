package com.hamusuke.numguesser.client.gui.component.list;

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
        SOUTH(0.0D, JList.HORIZONTAL_WRAP, BorderLayout.SOUTH),
        NORTH(Math.toRadians(180), JList.HORIZONTAL_WRAP, BorderLayout.NORTH),
        EAST(Math.toRadians(-90), JList.VERTICAL_WRAP, BorderLayout.EAST),
        WEST(Math.toRadians(90), JList.VERTICAL_WRAP, BorderLayout.WEST);

        public final double radToRotate;
        public final int layoutOrientation;
        public final String layoutDir;

        Direction(double rad, int layoutOrientation, String layoutDir) {
            this.radToRotate = rad;
            this.layoutOrientation = layoutOrientation;
            this.layoutDir = layoutDir;
        }
    }
}
