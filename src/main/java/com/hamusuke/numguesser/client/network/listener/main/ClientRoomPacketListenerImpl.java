package com.hamusuke.numguesser.client.network.listener.main;

import com.hamusuke.numguesser.client.NumGuesser;
import com.hamusuke.numguesser.client.gui.component.panel.dialog.CenteredMessagePanel;
import com.hamusuke.numguesser.network.channel.Connection;
import com.hamusuke.numguesser.network.listener.client.main.ClientRoomPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.play.PlayProtocols;
import com.hamusuke.numguesser.network.protocol.packet.room.clientbound.StartGameNotify;
import com.hamusuke.numguesser.network.protocol.packet.room.serverbound.ClientStartedGameNotify;

public class ClientRoomPacketListenerImpl extends ClientCommonPacketListenerImpl implements ClientRoomPacketListener {
    public ClientRoomPacketListenerImpl(NumGuesser client, Connection connection) {
        super(client, client.curRoom, connection);
        this.clientPlayer = client.clientPlayer;
    }

    @Override
    public void handleStartGame(StartGameNotify packet) {
        var listener = new ClientPlayPacketListenerImpl(this.client, this.curRoom, this.connection);
        this.connection.setupInboundProtocol(PlayProtocols.CLIENTBOUND, listener);
        this.connection.sendPacket(ClientStartedGameNotify.INSTANCE);
        this.connection.setupOutboundProtocol(PlayProtocols.SERVERBOUND);
        this.client.setPanel(new CenteredMessagePanel("ゲームを開始しています..."));
        this.client.playerTable.addPointColumn();
    }
}
