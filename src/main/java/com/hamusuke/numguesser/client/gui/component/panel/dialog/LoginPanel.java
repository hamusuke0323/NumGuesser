package com.hamusuke.numguesser.client.gui.component.panel.dialog;

import com.hamusuke.numguesser.client.gui.component.panel.Panel;
import com.hamusuke.numguesser.network.protocol.packet.login.serverbound.EnterNameRsp;
import com.mojang.brigadier.StringReader;
import org.jdesktop.swingx.JXButton;
import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXTextField;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import static com.hamusuke.numguesser.network.protocol.packet.login.serverbound.EnterNameRsp.MAX_NAME_LENGTH;

public class LoginPanel extends Panel {
    private JXTextField nameField;

    public LoginPanel() {
        super(new GridBagLayout());
    }

    @Override
    public void init() {
        super.init();

        this.nameField = new JXTextField("Name");
        this.nameField.setText("nanashi");
        this.nameField.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (nameField.getText().length() >= MAX_NAME_LENGTH || (!StringReader.isAllowedInUnquotedString(c) && (c != KeyEvent.VK_BACK_SPACE))) {
                    e.consume();
                }
            }
        });
        var login = new JXButton("ログイン");
        login.setActionCommand("login");
        login.addActionListener(this);

        var cancel = new JXButton("キャンセル");
        cancel.setActionCommand("cancel");
        cancel.addActionListener(this);

        var layout = (GridBagLayout) this.getLayout();
        addButton(this, new JXLabel("名前を入力してください（英数字のみ）", SwingConstants.CENTER), layout, 0, 0, 1, 1, 0.125D);
        addButton(this, this.nameField, layout, 0, 1, 1, 1, 0.125D);
        addButton(this, login, layout, 0, 2, 1, 1, 0.125D);
        addButton(this, cancel, layout, 0, 3, 1, 1, 0.125D);
    }

    @Override
    public JMenuBar createMenuBar() {
        var jMenuBar = new JMenuBar();
        jMenuBar.add(this.createMenuMenu());
        jMenuBar.add(this.createThemeMenu());
        return jMenuBar;
    }

    private void login() {
        var name = this.nameField.getText();
        if (!name.isEmpty()) {
            this.client.setPanel(new CenteredMessagePanel("ログインしています..."));
            this.client.getConnection().sendPacket(new EnterNameRsp(name.substring(0, Math.min(name.length(), 16))));
        }
    }

    @Override
    public void onClose() {
        this.client.disconnect();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "login":
                this.login();
                break;
            case "cancel":
                this.onClose();
                break;
        }
    }
}
