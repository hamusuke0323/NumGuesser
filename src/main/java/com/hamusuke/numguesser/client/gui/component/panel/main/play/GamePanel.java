package com.hamusuke.numguesser.client.gui.component.panel.main.play;

import com.google.common.collect.Lists;
import com.hamusuke.numguesser.client.game.card.AbstractClientCard;
import com.hamusuke.numguesser.client.game.card.LocalCard;
import com.hamusuke.numguesser.client.gui.component.list.CardList;
import com.hamusuke.numguesser.client.gui.component.list.CardList.Direction;
import com.hamusuke.numguesser.client.gui.component.panel.Panel;
import com.hamusuke.numguesser.client.network.player.RemotePlayer;
import com.hamusuke.numguesser.network.Player;
import com.hamusuke.numguesser.network.protocol.packet.serverbound.common.ReadyReq;
import com.hamusuke.numguesser.network.protocol.packet.serverbound.play.AttackReq;
import com.hamusuke.numguesser.network.protocol.packet.serverbound.play.CardSelectReq;
import com.hamusuke.numguesser.network.protocol.packet.serverbound.play.ClientCommandReq;
import com.hamusuke.numguesser.network.protocol.packet.serverbound.play.ClientCommandReq.Command;
import org.jdesktop.swingx.JXButton;
import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXPanel;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class GamePanel extends Panel {
    private int cardListIndex;
    private CardList myCards;
    private final List<CardList> remoteCards = Lists.newArrayList();
    private JXLabel statusLabel;
    private JXPanel cardShowCaseForFriend;
    private JXPanel cardShowCase;
    @Nullable
    private JXPanel curShownCardPanel;
    private JXButton attackBtn;
    private JXButton continueBtn;
    private JXButton stayBtn;
    private JXButton readyBtn;
    @Nullable
    private AbstractClientCard selectedCard;

    @Override
    public void init() {
        super.init();

        var commandPanel = new JXPanel(new GridBagLayout());
        var commandPanelLayout = (GridBagLayout) commandPanel.getLayout();

        this.statusLabel = new JXLabel();
        this.statusLabel.setHorizontalAlignment(SwingConstants.CENTER);

        this.cardShowCaseForFriend = new JXPanel();

        this.cardShowCase = new JXPanel();

        this.attackBtn = new JXButton("アタック");
        this.attackBtn.addActionListener(e -> this.attack());

        this.continueBtn = new JXButton("アタックを続ける");
        this.continueBtn.addActionListener(e -> this.continueAttacking());
        this.continueBtn.setVisible(false);
        this.stayBtn = new JXButton("ステイ");
        this.stayBtn.addActionListener(e -> this.stay());
        this.stayBtn.setVisible(false);

        this.readyBtn = new JXButton("準備完了");
        this.readyBtn.setVisible(false);
        this.readyBtn.addActionListener(e -> this.ready());

        addButton(commandPanel, this.statusLabel, commandPanelLayout, 0, 0, 2, 1, 1.0D, 0.05D);
        addButton(commandPanel, this.cardShowCaseForFriend, commandPanelLayout, 0, 1, 1, 1, 1.0D);
        addButton(commandPanel, this.cardShowCase, commandPanelLayout, 1, 1, 1, 1, 1.0D);
        addButton(commandPanel, this.attackBtn, commandPanelLayout, 0, 2, 2, 1, 1.0D, 0.05D);
        addButton(commandPanel, this.continueBtn, commandPanelLayout, 0, 3, 2, 1, 1.0D, 0.05D);
        addButton(commandPanel, this.stayBtn, commandPanelLayout, 0, 4, 2, 1, 1.0D, 0.05D);
        addButton(commandPanel, this.readyBtn, commandPanelLayout, 0, 5, 2, 1, 1.0D, 0.05D);

        this.add(commandPanel, BorderLayout.CENTER);
    }

    public void prepareAttacking(AbstractClientCard card) {
        this.setStatusLabel("あなたの番です。アタックしてください");
        this.setCardShowCase(card);
        this.setAttackBtnEnabled(true);
        this.continueBtn.setVisible(false);
        this.stayBtn.setVisible(false);

        if (this.selectedCard != null) {
            this.client.getConnection().sendPacket(new CardSelectReq(this.selectedCard.getId()));
        }
    }

    public void onRemotePlayerAttacking(RemotePlayer player) {
        this.setAttackBtnEnabled(false);
        this.setStatusLabel(player.getName() + "がアタックしています");
        this.continueBtn.setVisible(false);
        this.stayBtn.setVisible(false);
        this.setCardShowCase(null);
    }

    public void setAttackBtnEnabled(boolean enabled) {
        this.attackBtn.setEnabled(enabled);
        this.attackBtn.setVisible(true);
    }

    public void setStatusLabel(String text) {
        this.statusLabel.setText(text);
    }

    public void setCardShowCase(@Nullable AbstractClientCard card) {
        if (this.curShownCardPanel != null) {
            this.cardShowCase.remove(this.curShownCardPanel);
        }

        if (card == null) {
            this.curShownCardPanel = null;
            return;
        }

        this.curShownCardPanel = card.toPanel();
        this.cardShowCase.add(this.curShownCardPanel, BorderLayout.CENTER);
    }

    public void attackSucceeded() {
        this.setStatusLabel("アタック成功です。アタックを続けるかステイするかを選んでください");
        this.attackBtn.setVisible(false);
        this.attackBtn.setEnabled(false);
        this.continueBtn.setVisible(true);
        this.stayBtn.setVisible(true);
    }

    private void continueAttacking() {
        this.client.getConnection().sendPacket(new ClientCommandReq(Command.CONTINUE_ATTACKING));
    }

    private void stay() {
        this.client.getConnection().sendPacket(new ClientCommandReq(Command.STAY));
    }

    private void attack() {
        var card = this.selectedCard;
        if (card == null || card.isOpened()) {
            return;
        }

        var model = new DefaultListModel<AbstractClientCard>();
        for (int i = 0; i < 12; i++) {
            model.addElement(new LocalCard(card.getCardColor(), i));
        }

        var list = new CardList(Direction.SOUTH, model);

        var l = new GridBagLayout();
        var dialog = new JDialog(this.client.getMainWindow(), "数字を推理する", true);
        dialog.setLayout(l);

        var attack = new JXButton("アタックする");
        attack.addActionListener(e -> {
            if (list.isSelectionEmpty() || this.client.getConnection() == null) {
                return;
            }

            var guessed = (AbstractClientCard) list.getSelectedValue();
            this.client.getConnection().sendPacket(new AttackReq(card.getId(), guessed.getNum()));
            dialog.dispose();
        });

        var cancel = new JXButton("キャンセル");
        cancel.addActionListener(e -> dialog.dispose());

        addButton(dialog, new JXLabel("このカードは...", SwingConstants.CENTER), l, 0, 0, 2, 1, 1.0D, 0.05D);
        addButton(dialog, new JScrollPane(list), l, 0, 1, 2, 1, 1.0D);
        addButton(dialog, attack, l, 0, 2, 1, 1, 1.0D, 0.05D);
        addButton(dialog, cancel, l, 1, 2, 1, 1, 1.0D, 0.05D);

        dialog.pack();
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.setLocationRelativeTo(this.client.getMainWindow());
        dialog.setVisible(true);
    }

    public void onEndRound() {
        this.statusLabel.setText("ラウンド終了 全員が準備完了になると次のラウンドを開始します");
        this.readyBtn.setVisible(true);
        this.setCardShowCase(null);
        this.attackBtn.setVisible(false);
    }

    private void ready() {
        this.client.getConnection().sendPacket(new ReadyReq());
    }

    public void onReadySync() {
        synchronized (this.client.curRoom.getPlayers()) {
            var players = this.client.curRoom.getPlayers();
            int readyPlayers = (int) players.stream().filter(Player::isReady).count();
            this.readyBtn.setText("準備完了（%d / %d）".formatted(readyPlayers, players.size()));
        }
    }

    public void onReadyRsp() {
        this.readyBtn.setEnabled(false);
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
        if (card == null || this.selectedCard == card) {
            return;
        }

        this.selectedCard = card;
        this.client.getConnection().sendPacket(new CardSelectReq(card.getId()));
    }

    public void addCardList(boolean isLocal, String name, DefaultListModel<AbstractClientCard> cardList) {
        var l = new GridBagLayout();
        var panel = new JXPanel(l);

        var list = new CardList(isLocal ? Direction.SOUTH : Direction.values()[(this.cardListIndex + 1) % 4], cardList);
        list.addListSelectionListener(e -> {
            if (list.isSelectionEmpty() || e.getValueIsAdjusting()) {
                return;
            }

            this.onCardSelected(list);
        });

        if (isLocal) {
            this.myCards = list;
        } else {
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

        if (!isLocal) {
            this.cardListIndex++;
        }
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
