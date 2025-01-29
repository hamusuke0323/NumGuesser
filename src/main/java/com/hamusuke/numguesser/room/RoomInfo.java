package com.hamusuke.numguesser.room;

import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.codec.StreamCodec;
import com.hamusuke.numguesser.util.Util;
import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXPanel;

import javax.swing.*;
import java.awt.*;

public record RoomInfo(int id, String roomName, int population, boolean hasPassword) {
    public static final StreamCodec<IntelligentByteBuf, RoomInfo> STREAM_CODEC = StreamCodec.ofMember(RoomInfo::write, RoomInfo::new);

    private RoomInfo(IntelligentByteBuf buf) {
        this(buf.readVarInt(), buf.readString(), buf.readInt(), buf.readBoolean());
    }

    private void write(IntelligentByteBuf buf) {
        buf.writeVarInt(this.id);
        buf.writeString(this.roomName);
        buf.writeInt(this.population);
        buf.writeBoolean(this.hasPassword);
    }

    public JXPanel toPanel(Color selectionForeground) {
        var label = new JXLabel(Util.toHTML(String.format("%s\n%d人\n%s", this.roomName, this.population, this.hasPassword ? "パスワードあり" : "パスワードなし")), SwingConstants.CENTER);
        label.setForeground(selectionForeground);
        var panel = new JXPanel();
        panel.add(label);
        return panel;
    }

    @Override
    public String toString() {
        return "RoomInfo{" +
                "id=" + id +
                ", roomName='" + roomName + '\'' +
                ", population=" + population +
                ", hasPassword=" + hasPassword +
                '}';
    }
}
