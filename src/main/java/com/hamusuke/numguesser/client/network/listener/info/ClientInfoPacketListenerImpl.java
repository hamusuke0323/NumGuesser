package com.hamusuke.numguesser.client.network.listener.info;

import com.hamusuke.numguesser.Constants;
import com.hamusuke.numguesser.client.NumGuesser;
import com.hamusuke.numguesser.network.ServerInfo;
import com.hamusuke.numguesser.network.ServerInfo.Status;
import com.hamusuke.numguesser.network.channel.Connection;
import com.hamusuke.numguesser.network.listener.TickablePacketListener;
import com.hamusuke.numguesser.network.listener.client.info.ClientInfoPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.info.clientbound.InfoHandshakeDoneNotify;
import com.hamusuke.numguesser.network.protocol.packet.info.clientbound.ServerInfoRsp;
import com.hamusuke.numguesser.network.protocol.packet.info.serverbound.ServerInfoReq;
import com.hamusuke.numguesser.util.Util;

public class ClientInfoPacketListenerImpl implements ClientInfoPacketListener, TickablePacketListener {
    private static final int TIMEOUT_TICKS = 100;
    private final NumGuesser client;
    private final Connection connection;
    private final ServerInfo target;
    private int timeoutTicks = TIMEOUT_TICKS;

    public ClientInfoPacketListenerImpl(NumGuesser client, Connection connection, ServerInfo target) {
        this.client = client;
        this.connection = connection;
        this.target = target;
    }

    @Override
    public void tick() {
        if (this.timeoutTicks > 0 && this.target.status == Status.CONNECTING) {
            this.timeoutTicks--;
            if (this.timeoutTicks <= 0) {
                this.target.status = Status.FAILED;
                this.connection.disconnect("Failure");
            }
        }
    }

    @Override
    public void onDisconnect(String msg) {
        if (!msg.equals("Success")) {
            this.target.status = Status.FAILED;
        }

        this.client.onServerInfoChanged();
    }

    @Override
    public Connection getConnection() {
        return this.connection;
    }

    @Override
    public boolean isAcceptingMessages() {
        return this.connection.isConnected();
    }

    @Override
    public void handleInfoRsp(ServerInfoRsp packet) {
        this.target.protocolVersion = packet.protocolVersion();
        this.target.ping = (int) (Util.getMeasuringTimeMs() - packet.clientTimeEcho());
        this.target.status = this.target.protocolVersion == Constants.PROTOCOL_VERSION ? Status.OK : Status.MISMATCH_PROTOCOL_VERSION;
        this.connection.disconnect("Success");
    }

    @Override
    public void handleHandshakeDone(InfoHandshakeDoneNotify packet) {
        this.connection.sendPacket(new ServerInfoReq(Util.getMeasuringTimeMs()));
    }
}
