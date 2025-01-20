package com.hamusuke.numguesser.client.game.card;

import com.formdev.flatlaf.FlatLaf;
import com.hamusuke.numguesser.client.network.player.AbstractClientPlayer;
import com.hamusuke.numguesser.game.card.Card;
import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXPanel;

import javax.annotation.Nullable;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;

import static com.hamusuke.numguesser.Constants.*;

public abstract class AbstractClientCard extends Card {
    @Nullable
    protected AbstractClientPlayer selectedBy;
    private int newLabelTicks;

    public AbstractClientCard(CardColor cardColor) {
        super(cardColor);
    }

    public void tick() {
        if (this.isNewLabelShown()) {
            this.newLabelTicks--;
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
        return this.isOpened();
    }

    public void select(@Nullable AbstractClientPlayer selectedBy) {
        this.selectedBy = selectedBy;
    }

    public JXPanel toPanel() {
        return this.toPanel(false, false);
    }

    protected static Color getColorWithAlpha(Color color, boolean hasAlpha) {
        return new Color(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, hasAlpha ? 0.5F : 1.0F);
    }

    public JXPanel toPanel(boolean isSelected, boolean cellHasFocus) {
        var p = new JXPanel() {
            @Override
            protected void paintChildren(Graphics g) {
                var g2 = (Graphics2D) g.create();
                g2.setColor(getCardColor().getBgColor());
                g2.fillRoundRect(1, 1, CARD_WIDTH - 1, CARD_HEIGHT - 1, ARC_WIDTH, ARC_HEIGHT);
                g2.setColor(getCardColor().getTextColor());
                g2.setStroke(new BasicStroke(1.5F));
                g2.drawRoundRect(0, 0, CARD_WIDTH, CARD_HEIGHT, ARC_WIDTH, ARC_HEIGHT);
                g2.dispose();
                super.paintChildren(g);
            }
        };
        p.setPreferredSize(new Dimension(CARD_WIDTH, CARD_HEIGHT));
        p.setAlpha(isSelected && cellHasFocus ? 0.5F : 1.0F);

        int heightSub = this.selectedBy == null ? 0 : 50;
        if (this.isNewLabelShown()) {
            var newLabel = new JXLabel("NEW", SwingConstants.CENTER);
            newLabel.setBorder(new EtchedBorder());
            newLabel.setForeground(this.getCardColor().getTextColor());
            p.add(newLabel, BorderLayout.NORTH);
            heightSub += 50;
        } else if (this.isOpened() && this instanceof LocalCard) {
            var openedLabel = new JXLabel("オープン", SwingConstants.CENTER);
            openedLabel.setForeground(this.getCardColor().getTextColor());
            p.add(openedLabel, BorderLayout.NORTH);
            heightSub += 50;
        }

        if (this.canBeSeen()) {
            var l = new JXLabel("" + this.getNum(), SwingConstants.CENTER);
            l.setPreferredSize(new Dimension(CARD_WIDTH, CARD_HEIGHT - heightSub));
            l.setVerticalAlignment(SwingConstants.CENTER);
            l.setFont(l.getFont().deriveFont(FONT_SIZE));
            l.setForeground(this.getCardColor().getTextColor());
            p.add(l, BorderLayout.CENTER);
        }

        if (this.selectedBy != null) {
            var l = new JXLabel(this.selectedBy.getName() + "が選択中", SwingConstants.CENTER);
            l.setPreferredSize(new Dimension(CARD_WIDTH, 10));
            l.setForeground(this.getCardColor().getTextColor());
            p.add(l, BorderLayout.SOUTH);
        }

        var ret = new JXPanel();
        ret.add(p, BorderLayout.CENTER);
        return ret;
    }

    public void paint(Graphics2D g2d, boolean isSelected, boolean cellHasFocus) {
        var g2 = (Graphics2D) g2d.create();

        g2.setColor(getColorWithAlpha(this.getCardColor().getBgColor(), isSelected && cellHasFocus));
        g2.fillRoundRect(1, 1, CARD_WIDTH - 1, CARD_HEIGHT - 1, ARC_WIDTH, ARC_HEIGHT);
        g2.setColor(getColorWithAlpha(this.getCardColor().getTextColor(), isSelected && cellHasFocus));
        g2.setStroke(new BasicStroke(1.5F));
        g2.drawRoundRect(0, 0, CARD_WIDTH, CARD_HEIGHT, ARC_WIDTH, ARC_HEIGHT);

        if (this.isNewLabelShown()) {
            var strBounds = g2.getFont().getStringBounds("NEW", g2.getFontRenderContext());
            g2.drawString("NEW", (int) (CARD_WIDTH / 2.0D - strBounds.getCenterX()), (int) (strBounds.getMaxY() + 10));
        } else if (this.isOpened() && this instanceof LocalCard) {
            var strBounds = g2.getFont().getStringBounds("オープン", g2.getFontRenderContext());
            g2.drawString("オープン", (int) (CARD_WIDTH / 2.0D - strBounds.getCenterX()), (int) (strBounds.getMaxY() + 10));
        }

        if (this.canBeSeen()) {
            var number = "" + this.getNum();
            var tmp = g2.getFont();
            var font = g2.getFont().deriveFont(FONT_SIZE);
            g2.setFont(font);
            var strBounds = font.getStringBounds(number, g2.getFontRenderContext());
            g2.drawString(number, (int) (CARD_WIDTH / 2.0D - strBounds.getCenterX()), (int) (CARD_HEIGHT / 2.0D - strBounds.getCenterY()));
            g2.setFont(tmp);
        }

        if (this.selectedBy != null) {
            g2.setColor(FlatLaf.isLafDark() ? Color.WHITE : Color.BLACK);
            var arrow = "↓";
            var arrowStrBounds = g2.getFont().getStringBounds(arrow, g2.getFontRenderContext());
            g2.drawString(arrow, (int) (CARD_WIDTH / 2.0D - arrowStrBounds.getCenterX()), (int) (-arrowStrBounds.getMaxY()));

            var str = this.selectedBy.getName() + "が選択中";
            var strBounds = g2.getFont().getStringBounds(str, g2.getFontRenderContext());
            g2.drawString(str, (int) (CARD_WIDTH / 2.0D - strBounds.getCenterX()), (int) (-(arrowStrBounds.getMaxY() + strBounds.getMaxY() + 10)));
        }

        g2.dispose();
    }
}
