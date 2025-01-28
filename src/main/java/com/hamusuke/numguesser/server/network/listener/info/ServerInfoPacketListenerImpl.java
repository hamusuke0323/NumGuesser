package com.hamusuke.numguesser.server.network.listener.info;

import com.hamusuke.numguesser.Constants;
import com.hamusuke.numguesser.network.channel.Connection;
import com.hamusuke.numguesser.network.listener.server.info.ServerInfoPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.info.clientbound.ServerInfoRsp;
import com.hamusuke.numguesser.network.protocol.packet.info.serverbound.ServerInfoReq;

public class ServerInfoPacketListenerImpl implements ServerInfoPacketListener {
    private final Connection connection;

    public ServerInfoPacketListenerImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void handleInfoReq(ServerInfoReq packet) {
        this.connection.sendPacket(new ServerInfoRsp(Constants.PROTOCOL_VERSION, packet.clientTime()));
    }

    @Override
    public void onDisconnect(String msg) {
    }

    @Override
    public boolean isAcceptingMessages() {
        return this.connection.isConnected();
    }

    @Override
    public Connection getConnection() {
        return this.connection;
    }
}
