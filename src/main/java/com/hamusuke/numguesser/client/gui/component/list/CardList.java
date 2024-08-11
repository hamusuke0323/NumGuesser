package com.hamusuke.numguesser.client.gui.component.list;

import com.hamusuke.numguesser.game.card.Card;
import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXList;
import org.jdesktop.swingx.JXPanel;

import javax.swing.*;
import java.awt.*;

public class CardList extends JXList {
    private static final int CARD_WIDTH = 100;
    private static final int CARD_HEIGHT = 143;
    private static final float FONT_SIZE = 50.0F;
    private static final int ARC_WIDTH = 20;
    private static final int ARC_HEIGHT = 20;

    public CardList(DefaultListModel<Card> listModel) {
        super(listModel);

        this.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        this.setVisibleRowCount(1);
        this.setFixedCellWidth(CARD_WIDTH);
        this.setFixedCellHeight(CARD_HEIGHT);

        this.setCellRenderer((list, value, index, isSelected, cellHasFocus) -> {
            var card = (Card) value;
            var p = new JXPanel() {
                @Override
                protected void paintChildren(Graphics g) {
                    var g2 = (Graphics2D) g.create();
                    g2.setColor(card.getCardColor().getBgColor());
                    g2.fillRoundRect(1, 1, CARD_WIDTH - 1, CARD_HEIGHT - 1, ARC_WIDTH, ARC_HEIGHT);
                    g2.setColor(card.getCardColor().getTextColor());
                    g2.setStroke(new BasicStroke(1.5F));
                    g2.drawRoundRect(0, 0, CARD_WIDTH, CARD_HEIGHT, ARC_WIDTH, ARC_HEIGHT);
                    g2.dispose();
                    super.paintChildren(g);
                }
            };
            p.setAlpha(isSelected && cellHasFocus ? 0.5F : 1.0F);

            if (card.isOpened()) {
                var l = new JXLabel("" + card.getNum(), SwingConstants.CENTER);
                l.setPreferredSize(new Dimension(CARD_WIDTH, CARD_HEIGHT));
                l.setVerticalAlignment(SwingConstants.CENTER);
                l.setFont(l.getFont().deriveFont(FONT_SIZE));
                l.setForeground(card.getCardColor().getTextColor());
                p.add(l, BorderLayout.CENTER);
            }

            return p;
        });
    }
}
