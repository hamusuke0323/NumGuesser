package com.hamusuke.numguesser.client.gui.component.panel.main.play;

import com.google.common.collect.Lists;
import com.hamusuke.numguesser.client.game.card.AbstractClientCard;
import com.hamusuke.numguesser.client.game.card.LocalCard;
import com.hamusuke.numguesser.client.gui.component.list.CardList;
import com.hamusuke.numguesser.client.gui.component.panel.Panel;
import com.hamusuke.numguesser.client.network.player.RemotePlayer;
import com.hamusuke.numguesser.network.protocol.packet.serverbound.play.AttackReq;
import com.hamusuke.numguesser.network.protocol.packet.serverbound.play.CardSelectReq;
import com.hamusuke.numguesser.network.protocol.packet.serverbound.play.ClientCommandReq;
import com.hamusuke.numguesser.network.protocol.packet.serverbound.play.ClientCommandReq.Command;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdesktop.swingx.JXButton;
import org.jdesktop.swingx.JXDialog;
import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXPanel;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class GamePanel extends Panel {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final int STATUS_LABEL_INDEX = 0;
    private static final int CARD_SHOWCASE_INDEX = 1;
    private static final int BUTTONS_INDEX = 2;
    private static final int LOCAL_DECK_INDEX = 3;
    private int cardListIndex;
    private CardList myCards;
    private final List<CardList> remoteCards = Lists.newArrayList();
    private JXLabel statusLabel;
    private JXPanel cardShowCaseForFriend;
    private JXPanel cardShowCase;
    @Nullable
    private JXPanel curShownCardPanel;
    private JXButton attackBtn;
    @Nullable
    private AbstractClientCard selectedCard;

    public GamePanel() {
        super(new GridBagLayout());
    }

    @Override
    public void init() {
        super.init();

        var l = (GridBagLayout) this.getLayout();

        this.statusLabel = new JXLabel();
        this.statusLabel.setHorizontalAlignment(SwingConstants.CENTER);

        this.cardShowCaseForFriend = new JXPanel();

        this.cardShowCase = new JXPanel();

        this.attackBtn = new JXButton("アタック");
        this.attackBtn.addActionListener(e -> this.attack());

        addButton(this, this.statusLabel, l, 1, STATUS_LABEL_INDEX, 1, 1, 1.0D, 0.05D);
        addButton(this, this.cardShowCaseForFriend, l, 1, CARD_SHOWCASE_INDEX, 1, 1, 1.0D);
        addButton(this, this.cardShowCase, l, 2, CARD_SHOWCASE_INDEX, 1, 1, 1.0D);
        addButton(this, this.attackBtn, l, 1, BUTTONS_INDEX, 1, 1, 1.0D, 0.05D);
    }

    public void prepareAttacking(AbstractClientCard card) {
        this.setStatusLabel("あなたの番です。アタックしてください");
        this.setCardShowCase(card);
        this.setAttackBtnEnabled(true);
    }

    public void onRemotePlayerAttacking(RemotePlayer player) {
        this.setAttackBtnEnabled(false);
        this.setStatusLabel(player.getName() + "がアタックしています");
    }

    public void setAttackBtnEnabled(boolean enabled) {
        this.attackBtn.setEnabled(enabled);
    }

    public void setStatusLabel(String text) {
        this.statusLabel.setText(text);
    }

    public void setCardShowCase(@Nullable AbstractClientCard card) {
        if (card == null) {
            if (this.curShownCardPanel != null) {
                this.cardShowCase.remove(this.curShownCardPanel);
            }
            this.curShownCardPanel = null;
            return;
        }

        this.curShownCardPanel = card.toPanel();
        this.cardShowCase.add(this.curShownCardPanel, BorderLayout.CENTER);
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

        var list = new CardList(model);
        var l = new GridBagLayout();
        var p = new JXPanel(l);
        var dialog = new JXDialog(this.client.getMainWindow(), p);
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

        addButton(p, new JXLabel("このカードは...", SwingConstants.CENTER), l, 0, 0, 2, 1, 1.0D, 0.05D);
        addButton(p, new JScrollPane(list), l, 0, 1, 2, 1, 1.0D);
        addButton(p, attack, l, 0, 2, 1, 1, 1.0D, 0.05D);
        addButton(p, cancel, l, 1, 2, 1, 1, 1.0D, 0.05D);

        dialog.pack();
        dialog.setTitle("数字を推理する");
        dialog.setModal(true);
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.setLocationRelativeTo(this.client.getMainWindow());
        dialog.setVisible(true);
    }

    @Nullable
    private AbstractClientCard getSelectedCardOnlyRemote() {
        return this.remoteCards.stream()
                .filter(cardList -> !cardList.isSelectionEmpty())
                .findFirst()
                .map(cardList -> (AbstractClientCard) cardList.getSelectedValue())
                .orElse(null);
    }

    private void onCardSelected() {
        var card = this.getSelectedCardOnlyRemote();
        if (card == null) {
            return;
        }

        this.selectedCard = card;
        this.client.getConnection().sendPacket(new CardSelectReq(card.getId()));
    }

    public void addCardList(boolean isLocal, String name, DefaultListModel<AbstractClientCard> cardList) {
        var l = new GridBagLayout();
        var panel = new JXPanel(l);

        var nameLabel = new JXLabel((isLocal ? "自分の" : name + "の") + "カード (小 → 大)");
        nameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        var list = new CardList(cardList);
        list.addListSelectionListener(e -> {
            if (list.isSelectionEmpty() || e.getValueIsAdjusting()) {
                return;
            }

            this.onCardSelected();
        });
        if (isLocal) {
            this.myCards = list;
        } else {
            this.remoteCards.add(list);
        }

        addButton(panel, nameLabel, l, 0, 0, 1, 1, 1.0D, 0.05D);
        addButton(panel, new JScrollPane(list), l, 0, 1, 1, 1, 1.0D);
        addButton(this, panel, (GridBagLayout) this.getLayout(), 0, isLocal ? LOCAL_DECK_INDEX : this.cardListIndex, 1, 1, 1.0D);
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
