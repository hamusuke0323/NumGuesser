package com.hamusuke.numguesser.client.network.listener.main;

import com.hamusuke.numguesser.client.NumGuesser;
import com.hamusuke.numguesser.client.game.card.LocalCard;
import com.hamusuke.numguesser.client.game.card.RemoteCard;
import com.hamusuke.numguesser.client.gui.component.list.CardList;
import com.hamusuke.numguesser.client.room.ClientRoom;
import com.hamusuke.numguesser.game.card.Card;
import com.hamusuke.numguesser.network.channel.Connection;
import com.hamusuke.numguesser.network.listener.client.main.ClientPlayPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.clientbound.play.PlayerDeckSyncNotify;
import org.jdesktop.swingx.JXDialog;
import org.jdesktop.swingx.JXPanel;

import javax.swing.*;

public class ClientPlayPacketListenerImpl extends ClientCommonPacketListenerImpl implements ClientPlayPacketListener {
    public ClientPlayPacketListenerImpl(NumGuesser client, ClientRoom room, Connection connection) {
        super(client, room, connection);
        this.clientPlayer = client.clientPlayer;
    }

    @Override
    public void handlePlayerDeckSync(PlayerDeckSyncNotify packet) {
        var panel = new JXPanel();
        var model = new DefaultListModel<Card>();

        packet.cards().forEach(cardSerializer -> {
            if (cardSerializer.num() >= 0) {
                model.addElement(new LocalCard(cardSerializer.cardColor(), cardSerializer.num()));
            } else {
                model.addElement(new RemoteCard(cardSerializer.cardColor()));
            }
        });

        panel.add(new CardList(model));
        var dialog = new JXDialog(this.client.getMainWindow(), panel);
        dialog.setVisible(true);
    }
}
