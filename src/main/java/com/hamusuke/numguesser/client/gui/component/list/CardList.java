package com.hamusuke.numguesser.client.gui.component.list;

import com.hamusuke.numguesser.client.game.card.AbstractClientCard;
import org.jdesktop.swingx.JXList;

import javax.swing.*;

import static com.hamusuke.numguesser.Constants.CARD_HEIGHT;
import static com.hamusuke.numguesser.Constants.CARD_WIDTH;

public class CardList extends JXList {
    public CardList(DefaultListModel<AbstractClientCard> listModel) {
        super(listModel);

        this.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        this.setVisibleRowCount(1);
        this.setFixedCellWidth(CARD_WIDTH + 10);
        this.setFixedCellHeight(CARD_HEIGHT + 10);

        this.setCellRenderer((list, value, index, isSelected, cellHasFocus) -> {
            var card = (AbstractClientCard) value;
            return card.toPanel(isSelected, cellHasFocus);
        });
    }
}
