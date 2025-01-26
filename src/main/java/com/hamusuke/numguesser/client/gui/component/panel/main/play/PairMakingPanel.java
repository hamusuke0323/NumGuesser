package com.hamusuke.numguesser.client.gui.component.panel.main.play;

import com.hamusuke.numguesser.client.gui.component.list.PairList;
import com.hamusuke.numguesser.client.gui.component.panel.Panel;
import com.hamusuke.numguesser.client.network.player.AbstractClientPlayer;
import com.hamusuke.numguesser.network.protocol.packet.serverbound.play.ClientCommandReq;
import com.hamusuke.numguesser.network.protocol.packet.serverbound.play.ClientCommandReq.Command;
import com.hamusuke.numguesser.network.protocol.packet.serverbound.play.PairMakingDoneReq;
import org.jdesktop.swingx.JXBusyLabel;
import org.jdesktop.swingx.JXButton;
import org.jdesktop.swingx.JXLabel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class PairMakingPanel extends Panel {
    private final List<AbstractClientPlayer> member;
    private PairList list;

    public PairMakingPanel(List<AbstractClientPlayer> member) {
        super(new GridBagLayout());
        this.member = member;
    }

    @Override
    public void init() {
        super.init();

        var l = (GridBagLayout) this.getLayout();

        this.list = new PairList(this.client);
        this.list.addPairEntries(this.member);

        boolean owner = this.client.curRoom.amIOwner();

        var label = new JXBusyLabel();
        label.setBusy(true);
        label.setText("部屋のオーナーがペアを決めています...");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        addButton(this, label, l, 0, 0, 1, 1, 0.0125D);

        if (owner) {
            addButton(this, new JXLabel("プレイヤーをクリックして2人1組を作ります", SwingConstants.CENTER), l, 0, 1, 1, 1, 0.0125D);

            var done = new JXButton("ゲームを始める");
            done.addActionListener(e -> this.startGame());
            addButton(this, done, l, 0, 3, 1, 1, 0.025D);
        }

        addButton(this, this.list, l, 0, 2, 1, 1, 1.0D);
    }

    private void startGame() {
        this.client.getConnection().sendPacket(new PairMakingDoneReq());
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
