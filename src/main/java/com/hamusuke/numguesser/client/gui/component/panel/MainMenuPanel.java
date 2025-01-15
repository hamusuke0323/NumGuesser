package com.hamusuke.numguesser.client.gui.component.panel;

import org.jdesktop.swingx.JXButton;

import java.awt.*;

public class MainMenuPanel extends Panel {
    public MainMenuPanel() {
        super(new GridBagLayout());
    }

    @Override
    public void init() {
        super.init();

        var l = (GridBagLayout) this.getLayout();

        this.client.setWindowTitle(this.client.getGameTitle());
        var spEasy = new JXButton("一人プレイ 初級編");
        spEasy.addActionListener(e -> this.startSPEasy());
        var spHard = new JXButton("一人プレイ 上級編");
        spHard.addActionListener(e -> this.startSPHard());
        var mp = new JXButton("マルチプレイ");
        mp.addActionListener(e -> this.showServerList());

        addButton(this, spEasy, l, 0, 0, 1, 1, 1.0D);
        addButton(this, spHard, l, 0, 1, 1, 1, 1.0D);
        addButton(this, mp, l, 0, 2, 1, 1, 1.0D);
    }

    private void startSPEasy() {

    }

    private void startSPHard() {

    }

    private void showServerList() {
        this.client.setPanel(new ServerListPanel());
    }
}
