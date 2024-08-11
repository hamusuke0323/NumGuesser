package com.hamusuke.numguesser.client.network;

import com.hamusuke.numguesser.client.NumGuesser;
import com.hamusuke.numguesser.network.protocol.packet.serverbound.common.ChatReq;
import org.jdesktop.swingx.JXTextArea;
import org.jdesktop.swingx.JXTextField;

import javax.swing.*;

public class Chat {
    private final JXTextArea textArea;
    private final JScrollPane scrollTextArea;
    private final JXTextField field;
    private final JScrollPane scrollField;

    public Chat(NumGuesser client) {
        this.textArea = new JXTextArea();
        this.textArea.setLineWrap(true);
        this.textArea.setEditable(false);
        this.scrollTextArea = new JScrollPane(this.textArea);

        this.field = new JXTextField();
        this.field.addActionListener(e -> {
            var s = this.field.getText();
            if (!s.isEmpty()) {
                client.getConnection().sendPacket(new ChatReq(s));
                this.field.setText("");
            }
        });
        this.scrollField = new JScrollPane(this.field);
        this.scrollField.setAutoscrolls(true);
    }

    public void addMessage(String msg) {
        this.textArea.append(msg + "\n");
        this.scrollToMax();
    }

    public void scrollToMax() {
        this.textArea.setCaretPosition(this.textArea.getDocument().getLength());
    }

    public JScrollPane getTextArea() {
        return this.scrollTextArea;
    }

    public JScrollPane getField() {
        return this.scrollField;
    }

    public void clear() {
        this.textArea.setText("");
        this.scrollToMax();
    }
}
