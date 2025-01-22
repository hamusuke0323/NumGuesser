package com.hamusuke.numguesser.client.gui.component.panel.menu;

import com.hamusuke.numguesser.client.game.SinglePlayerGame.Difficulty;
import com.hamusuke.numguesser.client.gui.component.panel.Panel;
import com.hamusuke.numguesser.client.gui.component.panel.main.play.SinglePlayerGamePanel;
import org.jdesktop.swingx.JXButton;
import org.jdesktop.swingx.JXLabel;

import javax.swing.*;
import java.awt.*;

public class SPDifficultySelectionPanel extends Panel {
    public SPDifficultySelectionPanel() {
        super(new GridBagLayout());
    }

    @Override
    public void init() {
        super.init();

        var l = (GridBagLayout) this.getLayout();

        this.client.setWindowTitle("シングルプレイ - " + this.client.getGameTitleWithVersion());
        var sp = new JXButton("初級編");
        sp.addActionListener(e -> this.startSPEasy());
        var mp = new JXButton("上級編");
        mp.addActionListener(e -> this.startSPHard());
        var back = new JXButton("戻る");
        back.addActionListener(e -> this.client.setPanel(new MainMenuPanel()));
        var label = new JXLabel("難易度選択", SwingConstants.CENTER);

        addButton(this, label, l, 0, 0, 1, 1, 1.0D);
        addButton(this, sp, l, 0, 1, 1, 1, 1.0D);
        addButton(this, mp, l, 0, 2, 1, 1, 1.0D);
        addButton(this, back, l, 0, 3, 1, 1, 1.0D);
    }

    private void startSPEasy() {
        this.client.setPanel(new SinglePlayerGamePanel(Difficulty.EASY));
    }

    private void startSPHard() {
        this.client.setPanel(new SinglePlayerGamePanel(Difficulty.HARD));
    }
}
