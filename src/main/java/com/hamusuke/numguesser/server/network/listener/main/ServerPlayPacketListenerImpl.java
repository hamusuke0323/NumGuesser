package com.hamusuke.numguesser.server.network.listener.main;

import com.hamusuke.numguesser.network.channel.Connection;
import com.hamusuke.numguesser.network.listener.server.main.ServerPlayPacketListener;
import com.hamusuke.numguesser.server.NumGuesserServer;
import com.hamusuke.numguesser.server.network.ServerPlayer;

public class ServerPlayPacketListenerImpl extends ServerCommonPacketListenerImpl implements ServerPlayPacketListener {
    public ServerPlayPacketListenerImpl(NumGuesserServer server, Connection connection, ServerPlayer player) {
        super(server, connection, player);
    }


}
