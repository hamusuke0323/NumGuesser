package com.hamusuke.numguesser.client.game.card;

import com.hamusuke.numguesser.client.NumGuesser;
import com.hamusuke.numguesser.client.gui.component.list.CardList.Direction;
import com.hamusuke.numguesser.client.network.player.AbstractClientPlayer;
import com.hamusuke.numguesser.game.card.Card;
import org.jdesktop.swingx.JXPanel;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;

import static com.hamusuke.numguesser.Constants.*;

public abstract class AbstractClientCard extends Card {
    @Nullable
    protected AbstractClientPlayer selectedBy;
    private int newLabelTicks;
    protected boolean isTossed;

    public AbstractClientCard(CardColor cardColor) {
        super(cardColor);
    }

    public void tick() {
        if (this.isNewLabelShown()) {
            this.newLabelTicks--;

            if (!this.isNewLabelShown()) {
                SwingUtilities.invokeLater(NumGuesser.getInstance().getMainWindow().getPanel()::repaint);
            }
        }
    }

    public void showNewLabel() {
        this.newLabelTicks = 100;
    }

    public boolean isNewLabelShown() {
        return this.newLabelTicks > 0;
    }

    public abstract void setNum(int num);

    public boolean canBeSeen() {
        return this.isOpened() || this.isTossed;
    }

    public void tossed() {
        this.isTossed = true;
    }

    public void select(@Nullable AbstractClientPlayer selectedBy) {
        this.selectedBy = selectedBy;
    }

    public JXPanel toPanel() {
        return this.toPanel(Direction.SOUTH, false, false);
    }

    public JXPanel toPanel(Direction direction, boolean isSelected, boolean cellHasFocus) {
        var p = new JXCardPanel(this, direction);
        p.setPreferredSize(new Dimension(CARD_WIDTH, CARD_HEIGHT));
        if (direction == Direction.EAST || direction == Direction.WEST) {
            p.setPreferredSize(new Dimension(CARD_HEIGHT, CARD_WIDTH));
        }

        p.setAlpha(isSelected && cellHasFocus ? 0.5F : 1.0F);

        var ret = new JXPanel();
        ret.add(p, BorderLayout.CENTER);
        return ret;
    }

    private static class JXCardPanel extends JXPanel {
        private final AbstractClientCard card;
        private final Direction direction;

        public JXCardPanel(AbstractClientCard card, Direction direction) {
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

            boolean landscape = this.direction == Direction.EAST || this.direction == Direction.WEST;
            int offsetX = landscape ? 15 : 0;
            int offsetY = landscape ? -10 : 0;

            var selectedBy = this.card.selectedBy;
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
            } else if (this.card.isTossed && this.card instanceof RemoteCard) {
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
}
