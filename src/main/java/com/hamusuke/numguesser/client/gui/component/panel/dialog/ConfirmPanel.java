package com.hamusuke.numguesser.client.gui.component.panel.dialog;

import com.hamusuke.numguesser.client.gui.component.panel.Panel;
import org.jdesktop.swingx.JXButton;
import org.jdesktop.swingx.JXLabel;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class ConfirmPanel extends Panel {
    private final Panel parent;
    private final String text;
    protected final Consumer<Boolean> yesNo;
    protected boolean accepted;

    public ConfirmPanel(Panel parent, String text, Consumer<Boolean> yesNo) {
        super(new GridBagLayout());
        this.parent = parent;
        this.text = text;
        this.yesNo = yesNo;
    }

    @Override
    public void init() {
        super.init();

        var title = new JXLabel("確認", SwingConstants.CENTER);
        var label = new JXLabel(this.text, SwingConstants.CENTER);
        var yes = new JXButton("はい");
        yes.addActionListener(e -> {
            this.accepted = true;
            this.onClose();
        });
        var no = new JXButton("キャンセル");
        no.addActionListener(e -> {
            this.accepted = false;
            this.onClose();
        });
        var l = (GridBagLayout) getLayout();
        addButton(this, title, l, 0, 0, 1, 1, 0.125D);
        addButton(this, label, l, 0, 1, 1, 1, 0.125D);
        addButton(this, yes, l, 0, 2, 1, 1, 0.125D);
        addButton(this, no, l, 0, 3, 1, 1, 0.125D);
    }

    @Override
    public void onClose() {
        this.yesNo.accept(this.accepted);
        this.client.setPanel(this.parent);
    }
}
