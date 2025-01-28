package com.hamusuke.numguesser.server.network.listener.handshake;

import com.hamusuke.numguesser.Constants;
import com.hamusuke.numguesser.network.channel.Connection;
import com.hamusuke.numguesser.network.listener.server.handshake.ServerHandshakePacketListener;
import com.hamusuke.numguesser.network.protocol.packet.handshake.serverbound.HandshakeReq;
import com.hamusuke.numguesser.network.protocol.packet.info.InfoProtocols;
import com.hamusuke.numguesser.network.protocol.packet.info.clientbound.InfoHandshakeDoneNotify;
import com.hamusuke.numguesser.network.protocol.packet.login.LoginProtocols;
import com.hamusuke.numguesser.network.protocol.packet.login.clientbound.LoginDisconnectNotify;
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
        switch (packet.intention()) {
            case LOGIN:
                this.connection.setupOutboundProtocol(LoginProtocols.CLIENTBOUND);
                if (packet.protocolVersion() != Constants.PROTOCOL_VERSION) {
                    var msg = "プロトコルのバージョンが違います";
                    this.connection.sendPacket(new LoginDisconnectNotify(msg));
                    this.connection.disconnect(msg);
                } else {
                    LOGGER.info("Hello Packet came from {} and the connection established!", this.connection.getLoggableAddress(true));
                    this.connection.setupInboundProtocol(LoginProtocols.SERVERBOUND, new ServerLoginPacketListenerImpl(this.server, this.connection));
                }
                break;
            case INFO:
                this.connection.setupOutboundProtocol(InfoProtocols.CLIENTBOUND);
                this.connection.setupInboundProtocol(InfoProtocols.SERVERBOUND, new ServerInfoPacketListenerImpl(this.connection));
                this.connection.sendPacket(InfoHandshakeDoneNotify.INSTANCE);
                break;
            default:
                throw new UnsupportedOperationException("Invalid intention " + packet.intention());
        }
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
