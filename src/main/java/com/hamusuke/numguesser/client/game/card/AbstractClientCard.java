package com.hamusuke.numguesser.client.game.card;

import com.hamusuke.numguesser.client.NumGuesser;
import com.hamusuke.numguesser.client.gui.component.JXCardPanel;
import com.hamusuke.numguesser.client.gui.component.list.CardList.Direction;
import com.hamusuke.numguesser.client.network.player.AbstractClientPlayer;
import com.hamusuke.numguesser.game.card.Card;
import org.jdesktop.swingx.JXPanel;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;

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

    public boolean isTossed() {
        return this.isTossed;
    }

    public void select(@Nullable AbstractClientPlayer selectedBy) {
        this.selectedBy = selectedBy;
    }

    @Nullable
    public AbstractClientPlayer getSelectedBy() {
        return this.selectedBy;
    }

    public JXPanel toPanel() {
        return this.toPanel(Direction.SOUTH, false, false);
    }

    public JXPanel toPanel(Direction direction, boolean isSelected, boolean cellHasFocus) {
        var p = new JXCardPanel(this, direction);
        p.setPreferredSize(direction.panelSize);
        p.setAlpha(isSelected && cellHasFocus ? 0.5F : 1.0F);
        var ret = new JXPanel();
        ret.add(p, BorderLayout.CENTER);
        return ret;
    }
}
