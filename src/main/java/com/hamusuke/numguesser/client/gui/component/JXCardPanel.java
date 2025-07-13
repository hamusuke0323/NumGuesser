package com.hamusuke.numguesser.client.gui.component;

import com.hamusuke.numguesser.client.NumGuesser;
import com.hamusuke.numguesser.client.game.card.AbstractClientCard;
import com.hamusuke.numguesser.client.game.card.LocalCard;
import com.hamusuke.numguesser.client.game.card.RemoteCard;
import com.hamusuke.numguesser.client.gui.component.list.CardList;
import org.jdesktop.swingx.JXPanel;

import java.awt.*;
import java.awt.geom.Rectangle2D;

import static com.hamusuke.numguesser.Constants.*;

public class JXCardPanel extends JXPanel {
    private final AbstractClientCard card;
    private final CardList.Direction direction;

    public JXCardPanel(AbstractClientCard card, CardList.Direction direction) {
        this.card = card;
        this.direction = direction;
    }

    private static Rectangle2D drawCenteredString(Graphics2D g, String text, double centerX, double centerY) {
        var b = g.getFont().getStringBounds(text, g.getFontRenderContext());
        g.drawString(text, (float) (centerX - b.getCenterX()), (float) (centerY - b.getCenterY()));
        b.setRect(b.getX() + centerX - b.getCenterX(), b.getY() + centerY - b.getCenterY(), b.getWidth(), b.getHeight());
        return b;
    }

    @Override
    protected void paintChildren(Graphics g) {
        var g2 = (Graphics2D) g.create();

        var b = g2.getClipBounds();
        g2.rotate(this.direction.radToRotate, b.getCenterX(), b.getCenterY());

        boolean landscape = this.direction == CardList.Direction.EAST || this.direction == CardList.Direction.WEST;
        int offsetX = landscape ? 15 : 0;
        int offsetY = landscape ? -10 : 0;

        var selectedBy = this.card.getSelectedBy();
        boolean drawSelection = selectedBy != null && selectedBy != NumGuesser.getInstance().clientPlayer && selectedBy.getCardForAttack() != null;
        int offsetWithAttackerCard = drawSelection ? CARD_HEIGHT / 4 - 5 : 0;

        g2.setColor(this.card.getCardColor().getBgColor());
        g2.fillRoundRect(1 + offsetX, 1 + offsetY + offsetWithAttackerCard, CARD_WIDTH - 1, CARD_HEIGHT - 1, ARC_WIDTH, ARC_HEIGHT);

        g2.setColor(this.card.getCardColor().getTextColor());
        g2.setStroke(new BasicStroke(1.5F));
        g2.drawRoundRect(offsetX, offsetY + offsetWithAttackerCard, CARD_WIDTH, CARD_HEIGHT, ARC_WIDTH, ARC_HEIGHT);

        if (drawSelection) {
            g2.setColor(selectedBy.getCardForAttack().getCardColor().getBgColor());
            g2.fillRoundRect(1 + offsetX, 1 + offsetY - (CARD_HEIGHT - offsetWithAttackerCard), CARD_WIDTH - 1, CARD_HEIGHT - 1, ARC_WIDTH, ARC_HEIGHT);

            g2.setColor(selectedBy.getCardForAttack().getCardColor().getTextColor());
            g2.drawRoundRect(offsetX, offsetY - (CARD_HEIGHT - offsetWithAttackerCard), CARD_WIDTH, CARD_HEIGHT, ARC_WIDTH, ARC_HEIGHT);

            g2.setColor(this.card.getCardColor().getTextColor());
        }

        if (this.card.isNewLabelShown()) {
            drawCenteredString(g2, "NEW", b.getCenterX(), 10 + offsetWithAttackerCard);
        } else if (this.card.isOpened() && this.card instanceof LocalCard) {
            drawCenteredString(g2, "オープン", b.getCenterX(), 10 + offsetWithAttackerCard);
        } else if (this.card.isTossed() && this.card instanceof RemoteCard) {
            drawCenteredString(g2, "トス", b.getCenterX(), 10 + offsetWithAttackerCard);
        }

        if (this.card.canBeSeen()) {
            var tmp = g2.getFont();
            g2.setFont(g2.getFont().deriveFont(FONT_SIZE));
            int num = this.card.getNum();
            var numStrBounds = drawCenteredString(g2, "" + num, b.getCenterX(), b.getCenterY() + offsetWithAttackerCard);

            if (num == 6 || num == 9) { // 6 and 9 are similar, so draw underline to distinguish them.
                g2.drawLine((int) numStrBounds.getMinX(), (int) numStrBounds.getMaxY() - 2, (int) numStrBounds.getMaxX() + 3, (int) numStrBounds.getMaxY() - 2);
            }

            g2.setFont(tmp);
        }

        g2.dispose();

        super.paintChildren(g);
    }
}
