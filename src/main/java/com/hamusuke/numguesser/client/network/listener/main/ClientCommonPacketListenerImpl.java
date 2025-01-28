package com.hamusuke.numguesser.client.network.listener.main;

import com.hamusuke.numguesser.client.NumGuesser;
import com.hamusuke.numguesser.client.gui.component.panel.OwnerChangeListener;
import com.hamusuke.numguesser.client.gui.component.panel.dialog.OkPanel;
import com.hamusuke.numguesser.client.gui.component.panel.lobby.LobbyPanel;
import com.hamusuke.numguesser.client.gui.component.panel.main.room.RoomPanel;
import com.hamusuke.numguesser.client.gui.component.panel.menu.ServerListPanel;
import com.hamusuke.numguesser.client.network.listener.lobby.ClientLobbyPacketListenerImpl;
import com.hamusuke.numguesser.client.network.player.LocalPlayer;
import com.hamusuke.numguesser.client.network.player.RemotePlayer;
import com.hamusuke.numguesser.client.room.ClientRoom;
import com.hamusuke.numguesser.network.channel.Connection;
import com.hamusuke.numguesser.network.listener.TickablePacketListener;
import com.hamusuke.numguesser.network.listener.client.main.ClientCommonPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.common.clientbound.*;
import com.hamusuke.numguesser.network.protocol.packet.common.serverbound.LeftRoomNotify;
import com.hamusuke.numguesser.network.protocol.packet.common.serverbound.PongRsp;
import com.hamusuke.numguesser.network.protocol.packet.lobby.LobbyProtocols;

import javax.swing.*;

public abstract class ClientCommonPacketListenerImpl implements ClientCommonPacketListener, TickablePacketListener {
    protected final Connection connection;
    protected final NumGuesser client;
    protected LocalPlayer clientPlayer;
    protected int tickCount;
    protected final ClientRoom curRoom;

    protected ClientCommonPacketListenerImpl(NumGuesser client, ClientRoom room, Connection connection) {
        this.client = client;
        this.curRoom = room;
        this.client.listener = this;
        this.connection = connection;
    }

    @Override
    public void tick() {
        this.tickCount++;
    }

    @Override
    public void handleRoomOwnerChange(RoomOwnerChangeNotify packet) {
        var player = this.curRoom.getPlayer(packet.id());
        if (player == null) {
            return;
        }

        this.curRoom.setOwner(player);
        if (this.client.getPanel() instanceof OwnerChangeListener changeListener) {
            changeListener.onOwnerChanged();
        }
    }

    @Override
    public void handleGameModeChange(GameModeChangeNotify packet) {
        this.curRoom.setGameMode(packet.mode());

        if (this.client.getPanel() instanceof RoomPanel roomPanel) {
            roomPanel.onGameModeChanged();
        }
    }

    @Override
    public void handlePlayerReadySync(PlayerReadySyncNotify packet) {
        synchronized (this.curRoom.getPlayers()) {
            this.curRoom.getPlayers().stream()
                    .filter(p -> p.getId() == packet.id())
                    .forEach(player -> player.setReady(packet.ready()));
        }

        if (this.client.getPanel() instanceof RoomPanel roomPanel) {
            roomPanel.countReadyPlayers();

            if (!this.clientPlayer.isReady()) {
                roomPanel.reviveReadyButton();
            }
        }
    }

    @Override
    public void handleReadyRsp(ReadyRsp packet) {
        if (this.client.getPanel() instanceof RoomPanel roomPanel) {
            roomPanel.hideReadyButton();
        }
    }

    @Override
    public void handleChatPacket(ChatNotify packet) {
        this.client.chat.addMessage(packet.msg());
    }

    @Override
    public void handlePingPacket(PingReq packet) {
        if (!this.client.isSameThread()) {
            this.client.executeSync(() -> packet.handle(this));
        }

        this.connection.sendPacket(new PongRsp(packet.serverTime()));
    }

    @Override
    public void handleRTTPacket(RTTChangeNotify packet) {
        synchronized (this.curRoom.getPlayers()) {
            this.curRoom.getPlayers().stream()
                    .filter(p -> p.getId() == packet.id())
                    .forEach(player -> player.setPing(packet.rtt()));
        }

        SwingUtilities.invokeLater(this.client.playerTable::update);
    }

    @Override
    public void handleDisconnectPacket(DisconnectNotify packet) {
        this.connection.disconnect(packet.msg());
    }

    @Override
    public void handleJoinPacket(PlayerJoinNotify packet) {
        var remotePlayer = new RemotePlayer(packet.name());
        remotePlayer.setId(packet.id());
        this.curRoom.join(remotePlayer);
    }

    @Override
    public void handleLeavePacket(PlayerLeaveNotify packet) {
        this.curRoom.leave(packet.id());
    }

    @Override
    public void handleLeaveRoomSucc(LeaveRoomSuccNotify packet) {
        this.client.getMainWindow().reset(false);
        var listener = new ClientLobbyPacketListenerImpl(this.client, this.connection);
        this.connection.setupInboundProtocol(LobbyProtocols.CLIENTBOUND, listener);
        this.connection.sendPacket(LeftRoomNotify.INSTANCE);
        this.connection.setupOutboundProtocol(LobbyProtocols.SERVERBOUND);
        this.client.setPanel(new LobbyPanel());
    }

    @Override
    public void handlePlayerTipPointSync(PlayerTipPointSyncNotify packet) {
        var player = this.curRoom.getPlayer(packet.id());
        if (player != null) {
            player.setTipPoint(packet.tipPoint());
        }
    }

    @Override
    public void onDisconnect(String msg) {
        this.client.disconnect();

        var list = new ServerListPanel();
        var panel = msg.isEmpty() ? list : new OkPanel(list, "エラー", msg);
        this.client.getMainWindow().reset();
        this.client.setPanel(panel);
        this.client.clientPlayer = null;
        this.client.playerTable = null;
        this.client.chat = null;
        this.client.curRoom = null;
    }

    @Override
    public Connection getConnection() {
        return this.connection;
    }

    public NumGuesser getClient() {
        return this.client;
    }
}
