package com.hamusuke.numguesser.client.gui.component;

import com.google.common.collect.Lists;
import com.hamusuke.numguesser.client.game.card.AbstractClientCard;
import org.jdesktop.swingx.JXPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

import static com.hamusuke.numguesser.Constants.CARD_HEIGHT;
import static com.hamusuke.numguesser.Constants.CARD_WIDTH;

@Deprecated
public class JXGameTablePanel extends JXPanel implements MouseListener, ComponentListener {
    protected final List<DefaultListModel<AbstractClientCard>> cardLists = Lists.newArrayList();

    public JXGameTablePanel() {
        this.setBounds(0, 0, 400, 400);
        this.addMouseListener(this);
        this.addComponentListener(this);
    }

    protected static Direction getDirectionByIndex(int index) {
        return switch (index) {
            case 0 -> Direction.SOUTH;
            case 1 -> Direction.NORTH;
            case 2 -> Direction.EAST;
            case 3 -> Direction.WEST;
            default -> throw new IllegalStateException("Unexpected value: " + index);
        };
    }

    public void addCardList(boolean isLocal, DefaultListModel<AbstractClientCard> cardList) {
        this.cardLists.add(isLocal ? 0 : this.cardLists.size(), cardList);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        for (int i = 0; i < this.cardLists.size(); i++) {
            this.drawCards(getDirectionByIndex(i), this.cardLists.get(i), g);
        }
    }

    protected void drawCards(Direction direction, DefaultListModel<AbstractClientCard> list, Graphics g) {
        var g2d = (Graphics2D) g.create();
        var basePos = this.calculateCardListLocation(direction);
        g2d.translate(basePos.x, basePos.y);
        g2d.rotate(direction.radToRotate);

        for (int i = 0; i < list.getSize(); i++) {
            var card = list.getElementAt(i);
            g2d.translate(CARD_WIDTH, 0);
            card.paint(g2d, false, true);
        }
        g2d.dispose();
    }

    protected Point calculateCardListLocation(Direction direction) {
        var b = this.getBounds();
        return switch (direction) {
            case SOUTH -> new Point((int) (b.getCenterX() - CARD_WIDTH * 6), (int) (b.getMaxY() - CARD_HEIGHT * 2));
            case NORTH -> new Point((int) (b.getCenterX() + CARD_WIDTH * 6), (int) (b.getMinY() + CARD_HEIGHT * 2));
            case EAST -> new Point((int) (b.getMaxX() - CARD_HEIGHT * 2), (int) (b.getMaxY() - CARD_HEIGHT * 2 + 20));
            case WEST -> new Point((int) (b.getMinX() + CARD_HEIGHT * 2), (int) (b.getMinY() + CARD_HEIGHT * 2 - 20));
        };
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void componentResized(ComponentEvent e) {
        this.repaint();
    }

    @Override
    public void componentMoved(ComponentEvent e) {

    }

    @Override
    public void componentShown(ComponentEvent e) {

    }

    @Override
    public void componentHidden(ComponentEvent e) {

    }

    protected enum Direction {
        NORTH(Math.toRadians(180)),
        SOUTH(0.0D),
        EAST(Math.toRadians(-90)),
        WEST(Math.toRadians(90));

        public final double radToRotate;

        Direction(double rad) {
            this.radToRotate = rad;
        }
    }
}
