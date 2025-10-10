package com.hamusuke.numguesser.client.game.card;

import com.hamusuke.numguesser.client.gui.component.list.CardList.Direction;
import org.jdesktop.swingx.JXPanel;

import java.awt.*;

import static com.hamusuke.numguesser.Constants.*;

public class ClientFrameCard extends AbstractClientCard {
    protected int width;

    public ClientFrameCard() {
        this(CARD_WIDTH);
    }

    public ClientFrameCard(int width) {
        super(CardColor.BLACK);
        this.width = width;
    }

    @Override
    public int getNum() {
        return -2;
    }

    @Override
    public void setNum(int num) {
    }

    @Override
    public JXPanel toPanel(Direction direction, boolean isSelected, boolean cellHasFocus) {
        var ret = new JXPanel();
        var p = new JXPanel() {
            @Override
            protected void paintChildren(Graphics g) {
                var g2 = (Graphics2D) g.create();
                g2.setStroke(new BasicStroke(1.5F));
                g2.setColor(isSelected && cellHasFocus ? Color.CYAN : Color.GRAY);
                g2.drawRoundRect(0, 0, ClientFrameCard.this.width, CARD_HEIGHT, ARC_WIDTH, ARC_HEIGHT);
                g2.dispose();
                super.paintChildren(g);
            }
        };
        p.setPreferredSize(new Dimension(this.width, CARD_HEIGHT));
        ret.add(p, BorderLayout.CENTER);
        return ret;
    }
}
