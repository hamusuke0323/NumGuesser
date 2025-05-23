package com.hamusuke.numguesser.server.network.listener.main;

import com.hamusuke.numguesser.network.channel.Connection;
import com.hamusuke.numguesser.network.listener.server.main.ServerCommonPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.common.clientbound.ChatNotify;
import com.hamusuke.numguesser.network.protocol.packet.common.clientbound.LeaveRoomSuccNotify;
import com.hamusuke.numguesser.network.protocol.packet.common.clientbound.ReadyRsp;
import com.hamusuke.numguesser.network.protocol.packet.common.serverbound.*;
import com.hamusuke.numguesser.network.protocol.packet.disconnect.clientbound.DisconnectNotify;
import com.hamusuke.numguesser.network.protocol.packet.disconnect.serverbound.DisconnectReq;
import com.hamusuke.numguesser.network.protocol.packet.lobby.LobbyProtocols;
import com.hamusuke.numguesser.network.protocol.packet.loop.clientbound.PingReq;
import com.hamusuke.numguesser.network.protocol.packet.loop.serverbound.PongRsp;
import com.hamusuke.numguesser.server.NumGuesserServer;
import com.hamusuke.numguesser.server.network.ServerPlayer;
import com.hamusuke.numguesser.server.network.listener.lobby.ServerLobbyPacketListenerImpl;
import com.hamusuke.numguesser.server.room.ServerRoom;
import com.hamusuke.numguesser.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class ServerCommonPacketListenerImpl implements ServerCommonPacketListener {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final int TIMEOUT_TICKS = 600;
    public final Connection connection;
    protected final NumGuesserServer server;
    public final ServerPlayer player;
    public final ServerRoom room;
    private int timeoutTicks;
    private int tickCount;

    protected ServerCommonPacketListenerImpl(NumGuesserServer server, Connection connection, ServerPlayer player) {
        this.server = server;
        this.connection = connection;
        this.player = player;
        this.room = this.player.curRoom;
        player.connection = this;
    }

    @Override
    public void tick() {
        this.tickCount++;
        this.timeoutTicks++;

        if (this.timeoutTicks >= TIMEOUT_TICKS) {
            this.disconnect("タイムアウトしました");
        }

        if (this.tickCount % 20 == 0) {
            this.connection.sendPacket(new PingReq(Util.getMeasuringTimeMs()));
        }
    }

    private void disconnect(String msg) {
        try {
            this.connection.sendPacket(new DisconnectNotify(msg));
            this.connection.disconnect(msg);
        } catch (Exception e) {
            LOGGER.warn("Disconnect failed", e);
        }
    }

    @Override
    public void handleReady(ReadyReq packet) {
        this.player.setReady(true);
        this.player.sendPacket(ReadyRsp.INSTANCE);

        this.room.onPlayerReady();
    }

    @Override
    public void handleGameModeSelect(GameModeSelectReq packet) {
        if (this.player != this.room.getOwner()) {
            return;
        }

        this.room.setGameMode(packet.mode());
    }

    @Override
    public void handleLeaveRoom(LeaveRoomReq packet) {
        this.room.leave(this.player);
        this.player.sendPacket(LeaveRoomSuccNotify.INSTANCE);
        this.connection.setupOutboundProtocol(LobbyProtocols.CLIENTBOUND);
    }

    @Override
    public void handleLeftRoom(LeftRoomNotify packet) {
        new ServerLobbyPacketListenerImpl(this.server, this.connection, this.player);
    }

    @Override
    public void handleDisconnect(DisconnectReq packet) {
        this.connection.disconnect("");
    }

    @Override
    public void handleChatPacket(ChatReq packet) {
        if (packet.msg().startsWith("/")) {
            this.server.runCommand(this.player, packet.msg().substring(1));
            return;
        }

        this.room.sendPacketToAllInRoom(new ChatNotify(String.format("<%s> %s", this.player.getName(), packet.msg())));
    }

    @Override
    public void handlePong(PongRsp packet) {
        this.timeoutTicks = 0;
        this.player.setPing((int) (Util.getMeasuringTimeMs() - packet.serverTime()));
    }

    @Override
    public void onDisconnect(String msg) {
        LOGGER.info("{} lost connection", this.connection.getLoggableAddress(true));

        this.room.leave(this.player);
        this.server.getPlayerManager().removePlayer(this.player);
    }

    @Override
    public Connection getConnection() {
        return this.connection;
    }

    @Override
    public boolean isAcceptingMessages() {
        return this.connection.isConnected();
    }
}
