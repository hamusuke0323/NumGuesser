package com.hamusuke.numguesser.server.network.listener.main;

import com.hamusuke.numguesser.network.channel.Connection;
import com.hamusuke.numguesser.network.listener.server.main.ServerRoomPacketListener;
import com.hamusuke.numguesser.server.NumGuesserServer;
import com.hamusuke.numguesser.server.network.ServerPlayer;

public class ServerRoomPacketListenerImpl extends ServerCommonPacketListenerImpl implements ServerRoomPacketListener {
    public ServerRoomPacketListenerImpl(NumGuesserServer server, Connection connection, ServerPlayer player) {
        super(server, connection, player);
    }
}
