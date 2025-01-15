package com.hamusuke.numguesser.client.gui.component.list;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class NumberCardList extends JList implements DragGestureListener, DragSourceListener, Transferable {
    private static final Color LINE_COLOR = new Color(0x64_64_FF);
    private static final String NAME = "test";
    private static final String MIME_TYPE = DataFlavor.javaJVMLocalObjectMimeType;
    private static final DataFlavor FLAVOR = new DataFlavor(MIME_TYPE, NAME);
    protected final BufferedImage card;
    private final Rectangle targetLine = new Rectangle();
    private final boolean locked;
    protected int draggedIndex = -1;
    protected int targetIndex = -1;

    private NumberCardList(boolean locked) {
        this.locked = locked;
        this.setCellRenderer(null);
        new DropTarget(this, DnDConstants.ACTION_COPY_OR_MOVE, new ItemDropTargetListener(), true);
        DragSource.getDefaultDragSource().createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_COPY_OR_MOVE, this);

        this.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        this.setVisibleRowCount(1);

        BufferedImage card;
        var is = NumberCardList.class.getResourceAsStream("/card.jpg");
        if (is == null) {
            card = null;
        } else {
            try {
                card = ImageIO.read(is);
            } catch (IOException e) {
                card = null;
            }
        }

        this.card = card;
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

    private void onMoved(int from, int to) {
    }

    @Override
    public void dragGestureRecognized(DragGestureEvent e) {
        boolean oneOrMore = this.getSelectedIndices().length > 1;
        this.draggedIndex = this.locationToIndex(e.getDragOrigin());
        if (oneOrMore || this.draggedIndex < 0) {
            return;
        }
        try {
            e.startDrag(DragSource.DefaultMoveDrop, this, this);
        } catch (InvalidDnDOperationException ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public void dragEnter(DragSourceDragEvent e) {
        e.getDragSourceContext().setCursor(DragSource.DefaultMoveDrop);
    }

    @Override
    public void dragExit(DragSourceEvent e) {
        e.getDragSourceContext().setCursor(DragSource.DefaultMoveNoDrop);
    }

    @Override
    public void dragOver(DragSourceDragEvent e) {
    }

    @Override
    public void dragDropEnd(DragSourceDropEvent e) {
    }

    @Override
    public void dropActionChanged(DragSourceDragEvent e) {
    }

    @Override
    public Object getTransferData(DataFlavor flavor) {
        return this;
    }

    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[]{FLAVOR};
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return NAME.equals(flavor.getHumanPresentableName());
    }

    private final class ItemDropTargetListener implements DropTargetListener {
        @Override
        public void dragExit(DropTargetEvent e) {
            targetIndex = -1;
            repaint();
        }

        @Override
        public void dragEnter(DropTargetDragEvent e) {
            if (this.isDragAcceptable(e)) {
                e.acceptDrag(e.getDropAction());
            } else {
                e.rejectDrag();
            }
        }

        @Override
        public void dragOver(DropTargetDragEvent e) {
            if (this.isDragAcceptable(e)) {
                e.acceptDrag(e.getDropAction());
            } else {
                e.rejectDrag();
                return;
            }
            initTargetLine(e.getLocation());
            repaint();
        }

        @Override
        public void dropActionChanged(DropTargetDragEvent e) {
        }

        @Override
        public void drop(DropTargetDropEvent e) {
            if (this.isDropAcceptable(e) && targetIndex >= 0 && draggedIndex != targetIndex - 1) {
                if (targetIndex == draggedIndex) {
                    setSelectedIndex(targetIndex);
                } else if (targetIndex < draggedIndex) {
                    onMoved(draggedIndex, targetIndex);
                } else {
                    onMoved(draggedIndex, targetIndex - 1);
                }
                e.dropComplete(true);
            } else {
                e.dropComplete(false);
            }
            e.dropComplete(false);
            targetIndex = -1;
            repaint();
        }

        private boolean isDragAcceptable(DropTargetDragEvent e) {
            return !locked && isDataFlavorSupported(e.getCurrentDataFlavors()[0]);
        }

        private boolean isDropAcceptable(DropTargetDropEvent e) {
            return !locked && isDataFlavorSupported(e.getTransferable().getTransferDataFlavors()[0]);
        }
    }
}
