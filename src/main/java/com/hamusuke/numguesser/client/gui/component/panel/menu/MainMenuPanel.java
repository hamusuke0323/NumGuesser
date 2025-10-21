package com.hamusuke.numguesser.client.gui.component.panel.menu;

import com.hamusuke.numguesser.Constants;
import com.hamusuke.numguesser.client.gui.component.panel.Panel;
import org.jdesktop.swingx.JXButton;
import org.jdesktop.swingx.JXLabel;

import javax.swing.*;
import java.awt.*;

public class MainMenuPanel extends Panel {
    public MainMenuPanel() {
        super(new GridBagLayout());
    }

    @Override
    public void init() {
        super.init();

        var l = (GridBagLayout) this.getLayout();

        this.client.setWindowTitle("Main Menu - " + Constants.TITLE_AND_VERSION);
        var sp = new JXButton("シングルプレイ");
        sp.addActionListener(e -> this.goToSPPanel());
        var mp = new JXButton("マルチプレイ");
        mp.addActionListener(e -> this.showServerList());
        var gameTitle = new JXLabel(Constants.TITLE, SwingConstants.CENTER);

        addButton(this, gameTitle, l, 0, 0, 1, 1, 1.0D);
        addButton(this, sp, l, 0, 1, 1, 1, 1.0D);
        addButton(this, mp, l, 0, 2, 1, 1, 1.0D);
    }

    private void goToSPPanel() {
        this.client.setPanel(new SPDifficultySelectionPanel());
    }

    private void showServerList() {
        this.client.setPanel(new ServerListPanel());
    }
}
