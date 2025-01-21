package com.hamusuke.numguesser.client.gui.component.list;

import com.hamusuke.numguesser.client.game.card.AbstractClientCard;
import com.hamusuke.numguesser.client.game.card.ClientTransparentCard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

public class SinglePlayerGameCardList extends CardList {
    private static final Color LINE_COLOR = new Color(0x64_64_FF);
    private final Rectangle targetLine = new Rectangle();
    protected int draggedIndex = -1;
    protected int targetIndex = -1;

    public SinglePlayerGameCardList(DefaultListModel<AbstractClientCard> listModel) {
        super(Direction.SOUTH, listModel);

        this.addListSelectionListener(e -> this.clearSelection());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (this.targetIndex >= 0) {
            var g2 = (Graphics2D) g.create();
            g2.setPaint(LINE_COLOR);
            g2.fill(this.targetLine);
            g2.dispose();
        }
    }

    protected void initTargetLine(Point p) {
        var rect = getCellBounds(0, 0);
        int width = rect.width;
        int lineHeight = rect.height;
        int modelSize = this.getModel().getSize();
        this.targetIndex = -1;
        this.targetLine.setSize(2, lineHeight);
        for (int i = 0; i < modelSize; i++) {
            var card = this.getModel().getElementAt(i);
            if (card instanceof ClientTransparentCard) {
                continue;
            }

            rect.setLocation(width * i - width / 2, 0);
            if (rect.contains(p)) {
                this.targetIndex = i;
                this.targetLine.setLocation(i * width, 0);
                break;
            }
        }
        if (this.targetIndex < 0) {
            this.targetIndex = modelSize;
            this.targetLine.setLocation(this.targetIndex * width, 0);
        }
    }

    @Override
    protected void processMouseMotionEvent(MouseEvent e) {
        if (e.getID() != MouseEvent.MOUSE_MOVED) {
            System.out.println(e.getID());
        }

        switch (e.getID()) {
            case MouseEvent.MOUSE_ENTERED -> {

            }
            case MouseEvent.MOUSE_LAST -> {
                this.targetIndex = -1;
                this.repaint();
            }
            case MouseEvent.MOUSE_MOVED -> {
                this.initTargetLine(e.getPoint());
                this.repaint();
            }
        }
    }
}
