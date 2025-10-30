package com.hamusuke.numguesser.client.gui.component.panel.main.play;

import com.google.common.collect.Lists;
import com.hamusuke.numguesser.client.event.CardSelectEvent;
import com.hamusuke.numguesser.client.game.card.AbstractClientCard;
import com.hamusuke.numguesser.client.gui.component.list.CardList;
import com.hamusuke.numguesser.client.gui.component.list.CardList.Direction;
import com.hamusuke.numguesser.client.gui.component.panel.Panel;
import com.hamusuke.numguesser.network.protocol.packet.play.serverbound.ClientCommandReq;
import com.hamusuke.numguesser.network.protocol.packet.play.serverbound.ClientCommandReq.Command;
import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXPanel;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class GamePanel extends Panel {
    private final List<CardList> remoteCards = Lists.newArrayList();
    private JXPanel centerPanel;
    @Nullable
    private AbstractClientCard selectedCard;

    @Override
    public void init() {
        super.init();

        this.centerPanel = new JXPanel(new GridBagLayout());
        this.add(this.centerPanel, BorderLayout.CENTER);
    }

    public JXPanel getCenterPanel() {
        return this.centerPanel;
    }

    @Nullable
    private AbstractClientCard getSelectedCardOnlyRemote() {
        return this.remoteCards.stream()
                .filter(cardList -> cardList.hasFocus() && !cardList.isSelectionEmpty())
                .findFirst()
                .map(cardList -> (AbstractClientCard) cardList.getSelectedValue())
                .orElse(null);
    }

    private void clearSelection(CardList exclude) {
        for (var remoteCard : this.remoteCards) {
            if (remoteCard == exclude) {
                continue;
            }

            remoteCard.clearSelection();
        }
    }

    private void onCardSelected(CardList list) {
        this.clearSelection(list);
        var card = this.getSelectedCardOnlyRemote();
        if (card == null && list.hasFocus() && !list.isSelectionEmpty()) {
            this.selectedCard = (AbstractClientCard) list.getSelectedValue();
            return;
        }

        if (card == null || this.selectedCard == card) {
            return;
        }

        this.selectedCard = card;
        this.client.eventBus.post(new CardSelectEvent(card));
    }

    @Nullable
    public AbstractClientCard getSelectedCard() {
        return this.selectedCard;
    }

    public void addCardList(Direction direction, String name, DefaultListModel<AbstractClientCard> cardList) {
        var l = new GridBagLayout();
        var panel = new JXPanel(l);

        boolean isLocal = direction == Direction.SOUTH;

        var list = new CardList(direction, cardList);
        list.addListSelectionListener(e -> {
            if (list.isSelectionEmpty() || e.getValueIsAdjusting()) {
                return;
            }

            this.onCardSelected(list);
        });

        if (!isLocal) {
            this.remoteCards.add(list);
        }

        var scroll = new JScrollPane(list);
        scroll.getViewport().addChangeListener(e -> {
            SwingUtilities.invokeLater(list::repaint);
        });

        if (isLocal) {
            addButton(panel, scroll, l, 0, 0, 1, 2, 1.0D);
        } else {
            var nameLabel = new JXLabel(name);
            nameLabel.setHorizontalAlignment(SwingConstants.CENTER);
            addButton(panel, nameLabel, l, 0, 0, 1, 1, 1.0D, 0.05D);
            addButton(panel, scroll, l, 0, 1, 1, 1, 1.0D);
        }

        this.add(panel, list.getDirection().layoutDir);
        SwingUtilities.invokeLater(() -> {
            this.revalidate();
            this.repaint();
        });
    }

    @Override
    public JMenuBar createMenuBar() {
        var bar = new JMenuBar();
        bar.add(this.createMenuMenu());
        bar.add(this.createChatMenu());
        bar.add(this.createNetworkMenu());
        bar.add(this.createThemeMenu());
        return bar;
    }

    @Override
    protected JMenu createMenuMenu() {
        var m = super.createMenuMenu();
        var exit = new JMenuItem("ゲームをやめる");
        exit.setActionCommand("exit");
        exit.addActionListener(this);
        var leave = new JMenuItem("部屋から退出");
        leave.setActionCommand("leave");
        leave.addActionListener(this.client.getMainWindow());
        m.insert(exit, 0);
        m.insert(leave, 1);
        return m;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        this.client.getConnection().sendPacket(new ClientCommandReq(Command.EXIT_GAME));
    }
}
