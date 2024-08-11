package com.hamusuke.numguesser.server.network.listener.handshake;

import com.hamusuke.numguesser.Constants;
import com.hamusuke.numguesser.network.channel.Connection;
import com.hamusuke.numguesser.network.listener.server.handshake.ServerHandshakePacketListener;
import com.hamusuke.numguesser.network.protocol.packet.clientbound.info.InfoHandshakeDoneNotify;
import com.hamusuke.numguesser.network.protocol.packet.clientbound.login.LoginDisconnectNotify;
import com.hamusuke.numguesser.network.protocol.packet.serverbound.handshake.HandshakeReq;
import com.hamusuke.numguesser.server.NumGuesserServer;
import com.hamusuke.numguesser.server.network.listener.info.ServerInfoPacketListenerImpl;
import com.hamusuke.numguesser.server.network.listener.login.ServerLoginPacketListenerImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerHandshakePacketListenerImpl implements ServerHandshakePacketListener {
    private static final Logger LOGGER = LogManager.getLogger();
    private final NumGuesserServer server;
    private final Connection connection;

    public ServerHandshakePacketListenerImpl(NumGuesserServer server, Connection connection) {
        this.server = server;
        this.connection = connection;
    }

    @Override
    public void handleHandshake(HandshakeReq packet) {
        switch (packet.intendedProtocol()) {
            case LOGIN:
                this.connection.setProtocol(packet.nextProtocol());
                if (packet.protocolVersion() != Constants.PROTOCOL_VERSION) {
                    var msg = "プロトコルのバージョンが違います";
                    this.connection.sendPacket(new LoginDisconnectNotify(msg));
                    this.connection.disconnect(msg);
                } else {
                    LOGGER.info("Hello Packet came from {} and the connection established!", this.connection.getAddress());
                    this.connection.setListener(new ServerLoginPacketListenerImpl(this.server, this.connection));
                }
                break;
            case INFO:
                this.connection.setProtocol(packet.nextProtocol());
                this.connection.setListener(new ServerInfoPacketListenerImpl(this.connection));
                this.connection.sendPacket(new InfoHandshakeDoneNotify());
                break;
            default:
                throw new UnsupportedOperationException("Invalid intention " + packet.intendedProtocol());
        }
    }

    @Override
    public void onDisconnected(String msg) {
    }

    @Override
    public Connection getConnection() {
        return this.connection;
    }
}
