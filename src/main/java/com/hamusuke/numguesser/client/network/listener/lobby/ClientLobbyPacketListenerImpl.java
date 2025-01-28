package com.hamusuke.numguesser.client.network.listener.lobby;

import com.hamusuke.numguesser.client.NumGuesser;
import com.hamusuke.numguesser.client.gui.component.panel.dialog.CenteredMessagePanel;
import com.hamusuke.numguesser.client.gui.component.panel.dialog.EnterPasswordPanel;
import com.hamusuke.numguesser.client.gui.component.panel.dialog.OkPanel;
import com.hamusuke.numguesser.client.gui.component.panel.lobby.LobbyPanel;
import com.hamusuke.numguesser.client.gui.component.panel.main.room.RoomPanel;
import com.hamusuke.numguesser.client.gui.component.panel.menu.ServerListPanel;
import com.hamusuke.numguesser.client.gui.component.table.PlayerTable;
import com.hamusuke.numguesser.client.network.Chat;
import com.hamusuke.numguesser.client.network.listener.main.ClientRoomPacketListenerImpl;
import com.hamusuke.numguesser.client.network.player.LocalPlayer;
import com.hamusuke.numguesser.client.room.ClientRoom;
import com.hamusuke.numguesser.network.channel.Connection;
import com.hamusuke.numguesser.network.listener.TickablePacketListener;
import com.hamusuke.numguesser.network.listener.client.lobby.ClientLobbyPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.lobby.clientbound.*;
import com.hamusuke.numguesser.network.protocol.packet.lobby.serverbound.EnterPasswordRsp;
import com.hamusuke.numguesser.network.protocol.packet.lobby.serverbound.LobbyPingReq;
import com.hamusuke.numguesser.network.protocol.packet.lobby.serverbound.RoomJoinedNotify;
import com.hamusuke.numguesser.network.protocol.packet.room.RoomProtocols;

import javax.swing.*;

public class ClientLobbyPacketListenerImpl implements ClientLobbyPacketListener, TickablePacketListener {
    private final NumGuesser client;
    private final Connection connection;
    private final LocalPlayer clientPlayer;
    private int tickCount;

    public ClientLobbyPacketListenerImpl(NumGuesser client, Connection connection) {
        this.client = client;
        this.connection = connection;
        this.clientPlayer = client.clientPlayer;
    }

    @Override
    public void tick() {
        this.tickCount++;
        if (this.tickCount % 20 == 0) {
            this.connection.sendPacket(LobbyPingReq.INSTANCE);
        }
    }

    @Override
    public Connection getConnection() {
        return this.connection;
    }

    @Override
    public void handleDisconnectPacket(LobbyDisconnectNotify packet) {
        this.connection.disconnect(packet.msg());
    }

    @Override
    public void handlePong(LobbyPongRsp packet) {
    }

    @Override
    public void handleRoomList(RoomListNotify packet) {
        if (this.client.getPanel() instanceof LobbyPanel panel) {
            panel.addAll(packet.infoList());
        }
    }

    @Override
    public void handleJoinRoomSucc(JoinRoomSuccNotify packet) {
        this.client.curRoom = ClientRoom.fromRoomInfo(this.client, packet.info());
        this.client.curRoom.join(this.clientPlayer);
        this.client.playerTable = new PlayerTable(this.client);
        SwingUtilities.invokeLater(this.client.playerTable::clear);
        this.client.chat = new Chat(this.client);
        this.client.setPanel(new RoomPanel());
        var listener = new ClientRoomPacketListenerImpl(this.client, this.connection);
        this.connection.setupInboundProtocol(RoomProtocols.CLIENTBOUND, listener);
        this.connection.sendPacket(RoomJoinedNotify.INSTANCE);
        this.connection.setupOutboundProtocol(RoomProtocols.SERVERBOUND);
    }

    @Override
    public void handleJoinRoomFail(JoinRoomFailNotify packet) {
        this.client.setPanel(new OkPanel(new LobbyPanel(), "エラー", packet.msg()));
    }

    @Override
    public void handleEnterPassword(EnterPasswordReq packet) {
        var enterPasswordPanel = new EnterPasswordPanel(p -> {
            if (!p.isAccepted()) {
                this.client.setPanel(new LobbyPanel());
                return;
            }

            this.client.setPanel(new CenteredMessagePanel("部屋に参加しています..."));
            this.connection.sendPacket(new EnterPasswordRsp(packet.roomId(), p.getPassword()));
        });
        var panel = packet.msg().isEmpty() ? enterPasswordPanel : new OkPanel(enterPasswordPanel, "エラー", packet.msg());
        this.client.setPanel(panel);
    }

    @Override
    public void onDisconnect(String msg) {
        this.client.disconnect();
        var list = new ServerListPanel();
        var panel = msg.isEmpty() ? list : new OkPanel(list, "エラー", msg);
        this.client.getMainWindow().reset();
        this.client.setPanel(panel);
        this.client.clientPlayer = null;
    }

    @Override
    public boolean isAcceptingMessages() {
        return this.connection.isConnected();
    }
}
