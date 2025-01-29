package com.hamusuke.numguesser.client.gui.component.panel.main.play;

import com.google.common.collect.Lists;
import com.hamusuke.numguesser.client.game.card.AbstractClientCard;
import com.hamusuke.numguesser.client.game.card.LocalCard;
import com.hamusuke.numguesser.client.gui.component.list.CardList;
import com.hamusuke.numguesser.client.gui.component.list.CardList.Direction;
import com.hamusuke.numguesser.client.gui.component.panel.Panel;
import com.hamusuke.numguesser.client.network.listener.main.ClientPlayPacketListenerImpl;
import com.hamusuke.numguesser.client.network.player.RemotePlayer;
import com.hamusuke.numguesser.game.GameMode;
import com.hamusuke.numguesser.network.Player;
import com.hamusuke.numguesser.network.protocol.packet.common.serverbound.ReadyReq;
import com.hamusuke.numguesser.network.protocol.packet.play.serverbound.*;
import com.hamusuke.numguesser.network.protocol.packet.play.serverbound.ClientCommandReq.Command;
import org.jdesktop.swingx.JXButton;
import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXPanel;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class GamePanel extends Panel {
    private final List<CardList> remoteCards = Lists.newArrayList();
    private JXLabel statusLabel;
    private JXPanel cardShowCase;
    @Nullable
    private JXPanel curShownCardPanel;
    private JXButton attackBtn;
    private JXButton cancelBtn;
    private JXButton letAllyToss;
    private JXButton attackWithoutToss;
    private JXButton toss;
    private JXButton selectThisCardBtn;
    private JXButton continueBtn;
    private JXButton stayBtn;
    private JXButton readyBtn;
    private JXButton backBtn;
    @Nullable
    private AbstractClientCard selectedCard;

    @Override
    public void init() {
        super.init();

        var commandPanel = new JXPanel(new GridBagLayout());
        var commandPanelLayout = (GridBagLayout) commandPanel.getLayout();

        this.statusLabel = new JXLabel();
        this.statusLabel.setHorizontalAlignment(SwingConstants.CENTER);

        this.cardShowCase = new JXPanel();

        this.attackBtn = new JXButton("アタック");
        this.attackBtn.addActionListener(e -> this.attack());

        this.cancelBtn = new JXButton("キャンセル");
        this.cancelBtn.addActionListener(e -> this.onCancelBtnPressed());
        this.cancelBtn.setVisible(false);

        this.letAllyToss = new JXButton("トスしてもらう");
        this.letAllyToss.addActionListener(e -> this.letAllyToss());
        this.letAllyToss.setVisible(false);

        this.attackWithoutToss = new JXButton("アタック");
        this.attackWithoutToss.addActionListener(e -> this.attackWithoutToss());
        this.attackWithoutToss.setVisible(false);

        this.toss = new JXButton("このカードをトスする");
        this.toss.addActionListener(e -> this.toss());
        this.toss.setVisible(false);

        this.selectThisCardBtn = new JXButton("このカードでアタックする");
        this.selectThisCardBtn.addActionListener(e -> this.selectThisCardForAttack());
        this.selectThisCardBtn.setVisible(false);

        this.continueBtn = new JXButton("アタックを続ける");
        this.continueBtn.addActionListener(e -> this.continueAttacking());
        this.continueBtn.setVisible(false);
        this.stayBtn = new JXButton("ステイ");
        this.stayBtn.addActionListener(e -> this.stay());
        this.stayBtn.setVisible(false);

        this.readyBtn = new JXButton("準備完了");
        this.readyBtn.setVisible(false);
        this.readyBtn.addActionListener(e -> this.ready());

        this.backBtn = new JXButton("戻る");
        this.backBtn.setVisible(false);
        this.backBtn.addActionListener(this);

        addButton(commandPanel, this.statusLabel, commandPanelLayout, 0, 0, 1, 1, 1.0D, 0.05D);
        addButton(commandPanel, this.cardShowCase, commandPanelLayout, 0, 1, 1, 1, 1.0D);
        addButton(commandPanel, this.letAllyToss, commandPanelLayout, 0, 2, 1, 1, 1.0D, 0.05D);
        addButton(commandPanel, this.attackWithoutToss, commandPanelLayout, 0, 3, 1, 1, 1.0D, 0.05D);
        addButton(commandPanel, this.toss, commandPanelLayout, 0, 4, 1, 1, 1.0D, 0.05D);
        addButton(commandPanel, this.selectThisCardBtn, commandPanelLayout, 0, 5, 1, 1, 1.0D, 0.05D);
        addButton(commandPanel, this.attackBtn, commandPanelLayout, 0, 6, 1, 1, 1.0D, 0.05D);
        addButton(commandPanel, this.cancelBtn, commandPanelLayout, 0, 7, 1, 1, 1.0D, 0.05D);
        addButton(commandPanel, this.continueBtn, commandPanelLayout, 0, 8, 1, 1, 1.0D, 0.05D);
        addButton(commandPanel, this.stayBtn, commandPanelLayout, 0, 9, 1, 1, 1.0D, 0.05D);
        addButton(commandPanel, this.readyBtn, commandPanelLayout, 0, 10, 1, 1, 1.0D, 0.05D);
        addButton(commandPanel, this.backBtn, commandPanelLayout, 0, 11, 1, 1, 1.0D, 0.05D);

        this.add(commandPanel, BorderLayout.CENTER);
    }

    private void onCancelBtnPressed() {
        if (this.client.getConnection() == null) {
            return;
        }

        this.client.getConnection().sendPacket(new ClientCommandReq(Command.CANCEL));
    }

    private void selectThisCardForAttack() {
        if (this.selectedCard == null || !this.client.clientPlayer.getDeck().contains(this.selectedCard) || this.selectedCard.isOpened()) {
            return;
        }

        this.setStatusLabel("カードをサーバーに送信しています...");
        this.selectThisCardBtn.setVisible(false);
        this.client.getConnection().sendPacket(new CardForAttackSelectRsp(this.selectedCard.getId()));
    }

    public void onSelectCardForAttackReq(boolean cancellable) {
        this.selectedCard = null;
        this.setStatusLabel("ふせたカードの中から、アタックするためのカードを選んでください");
        this.setCardShowCase(null);
        this.setAttackBtnEnabled(false);
        this.continueBtn.setVisible(false);
        this.stayBtn.setVisible(false);
        this.cancelBtn.setVisible(cancellable);
        this.letAllyToss.setVisible(false);
        this.attackWithoutToss.setVisible(false);
        this.toss.setVisible(false);

        this.selectThisCardBtn.setVisible(true);
    }

    public void onSelectTossOrAttack() {
        this.selectedCard = null;
        this.setStatusLabel("トスをしてもらうかアタックするかを選んでください");
        this.setCardShowCase(null);
        this.setAttackBtnEnabled(false);
        this.continueBtn.setVisible(false);
        this.stayBtn.setVisible(false);
        this.cancelBtn.setVisible(false);
        this.selectThisCardBtn.setVisible(false);
        this.toss.setVisible(false);

        this.letAllyToss.setVisible(true);
        this.attackWithoutToss.setVisible(true);
    }

    private void letAllyToss() {
        this.client.getConnection().sendPacket(new ClientCommandReq(Command.LET_ALLY_TOSS));
    }

    private void attackWithoutToss() {
        this.client.getConnection().sendPacket(new ClientCommandReq(Command.ATTACK_WITHOUT_TOSS));
    }

    public void onTossReq() {
        this.selectedCard = null;
        this.setStatusLabel("味方にトスするカードを選んでください");
        this.setCardShowCase(null);
        this.setAttackBtnEnabled(false);
        this.continueBtn.setVisible(false);
        this.stayBtn.setVisible(false);
        this.cancelBtn.setVisible(false);
        this.selectThisCardBtn.setVisible(false);
        this.letAllyToss.setVisible(false);
        this.attackWithoutToss.setVisible(false);

        this.toss.setVisible(true);
    }

    private void toss() {
        if (this.selectedCard == null || !this.client.clientPlayer.getDeck().contains(this.selectedCard) || this.selectedCard.isOpened()) {
            return;
        }

        this.client.getConnection().sendPacket(new TossRsp(this.selectedCard.getId()));
    }

    public void onRemotePlayerSelectCardForAttack(RemotePlayer remotePlayer) {
        this.setAttackBtnEnabled(false);
        this.setStatusLabel(remotePlayer.getName() + "がアタックするためのカードを選んでいます");
        this.continueBtn.setVisible(false);
        this.stayBtn.setVisible(false);
        this.cancelBtn.setVisible(false);
        this.toss.setVisible(false);
        this.setCardShowCase(null);
    }

    public void prepareAttacking(AbstractClientCard card, boolean cancellable) {
        this.setStatusLabel("あなたの番です。アタックしてください");
        this.setCardShowCase(card);
        this.setAttackBtnEnabled(true);
        this.continueBtn.setVisible(false);
        this.stayBtn.setVisible(false);
        this.cancelBtn.setVisible(cancellable);

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
        this.attackBtn.setVisible(enabled);
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
        this.cancelBtn.setVisible(false);
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
        if (card == null || card.isOpened() || !this.isAttackable(card)) {
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

    private boolean isAttackable(AbstractClientCard card) {
        if (this.client.curRoom.getGameMode() == GameMode.NORMAL_GAME) {
            return !this.client.clientPlayer.getDeck().contains(card);
        }

        if (this.client.listener instanceof ClientPlayPacketListenerImpl play) {
            var cardHolder = play.getCardPlayerMap().get(card.getId());
            return cardHolder != null && this.client.clientPlayer.getPairColor() != cardHolder.getPairColor();
        }

        return false;
    }

    public void onEndRound(boolean isFinalRound) {
        this.statusLabel.setText(isFinalRound ? "全てのラウンドが終了しました" : "ラウンド終了 全員が準備完了になると次のラウンドを開始します");
        this.readyBtn.setVisible(!isFinalRound);

        this.setCardShowCase(null);
        this.cancelBtn.setVisible(false);
        this.setAttackBtnEnabled(false);

        this.backBtn.setVisible(isFinalRound);
    }

    private void ready() {
        this.client.getConnection().sendPacket(ReadyReq.INSTANCE);
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
        if (card == null && list.hasFocus() && !list.isSelectionEmpty()) {
            this.selectedCard = (AbstractClientCard) list.getSelectedValue();
            return;
        }

        if (card == null || this.selectedCard == card) {
            return;
        }

        this.selectedCard = card;
        this.client.getConnection().sendPacket(new CardSelectReq(card.getId()));
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
