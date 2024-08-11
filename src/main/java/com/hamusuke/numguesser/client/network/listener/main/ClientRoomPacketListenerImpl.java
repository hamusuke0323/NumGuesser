package com.hamusuke.numguesser.client.network.listener.main;

import com.hamusuke.numguesser.client.NumGuesser;
import com.hamusuke.numguesser.network.channel.Connection;
import com.hamusuke.numguesser.network.listener.client.main.ClientRoomPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.clientbound.room.StartGameNotify;

public class ClientRoomPacketListenerImpl extends ClientCommonPacketListenerImpl implements ClientRoomPacketListener {
    public ClientRoomPacketListenerImpl(NumGuesser client, Connection connection) {
        super(client, client.curRoom, connection);
        this.clientPlayer = client.clientPlayer;
    }

    @Override
    public void handleStartGame(StartGameNotify packet) {
        var listener = new ClientPlayPacketListenerImpl(this.client, this.curRoom, this.connection);
        this.connection.setListener(listener);
        this.connection.setProtocol(packet.nextProtocol());
    }
}
