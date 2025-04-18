package com.hamusuke.numguesser.client.network.listener.login;

import com.hamusuke.numguesser.client.NumGuesser;
import com.hamusuke.numguesser.client.gui.component.panel.dialog.LoginPanel;
import com.hamusuke.numguesser.client.gui.component.panel.dialog.OkPanel;
import com.hamusuke.numguesser.client.gui.component.panel.lobby.LobbyPanel;
import com.hamusuke.numguesser.client.gui.component.panel.menu.ServerListPanel;
import com.hamusuke.numguesser.client.network.listener.lobby.ClientLobbyPacketListenerImpl;
import com.hamusuke.numguesser.client.network.player.LocalPlayer;
import com.hamusuke.numguesser.network.PacketSendListener;
import com.hamusuke.numguesser.network.channel.Connection;
import com.hamusuke.numguesser.network.encryption.NetworkEncryptionUtil;
import com.hamusuke.numguesser.network.listener.client.login.ClientLoginPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.disconnect.clientbound.DisconnectNotify;
import com.hamusuke.numguesser.network.protocol.packet.lobby.LobbyProtocols;
import com.hamusuke.numguesser.network.protocol.packet.login.clientbound.EnterNameReq;
import com.hamusuke.numguesser.network.protocol.packet.login.clientbound.KeyExchangeRsp;
import com.hamusuke.numguesser.network.protocol.packet.login.clientbound.LoginCompressionNotify;
import com.hamusuke.numguesser.network.protocol.packet.login.clientbound.LoginSuccessNotify;
import com.hamusuke.numguesser.network.protocol.packet.login.serverbound.EncryptionSetupReq;
import com.hamusuke.numguesser.network.protocol.packet.login.serverbound.LobbyJoinedNotify;
import com.hamusuke.numguesser.network.protocol.packet.loop.clientbound.PingReq;
import com.hamusuke.numguesser.network.protocol.packet.loop.serverbound.PongRsp;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.crypto.Cipher;
import java.util.function.Consumer;

public class ClientLoginPacketListenerImpl implements ClientLoginPacketListener {
    private static final Logger LOGGER = LogManager.getLogger();
    private final NumGuesser client;
    private final Consumer<String> statusConsumer;
    private final Connection connection;
    private boolean waitingAuthComplete;

    public ClientLoginPacketListenerImpl(Connection connection, NumGuesser client, Consumer<String> statusConsumer) {
        this.client = client;
        this.connection = connection;
        this.statusConsumer = statusConsumer;
    }

    @Override
    public void handleKeyEx(KeyExchangeRsp packet) {
        Cipher cipher;
        Cipher cipher2;
        EncryptionSetupReq req;
        try {
            var secretKey = NetworkEncryptionUtil.generateKey();
            var publicKey = packet.getPublicKey();
            cipher = NetworkEncryptionUtil.cipherFromKey(2, secretKey);
            cipher2 = NetworkEncryptionUtil.cipherFromKey(1, secretKey);
            req = new EncryptionSetupReq(secretKey, publicKey, packet.nonce());
        } catch (Exception e) {
            LOGGER.error("Protocol error", e);
            throw new IllegalStateException("Protocol error", e);
        }

        this.statusConsumer.accept("通信を暗号化しています...");
        this.connection.sendPacket(req, PacketSendListener.thenRun(() -> this.connection.setEncryptionKey(cipher, cipher2)));
    }

    @Override
    public void handleSuccess(LoginSuccessNotify packet) {
        this.waitingAuthComplete = false;
        this.statusConsumer.accept("ロビーに参加しています...");
        this.client.clientPlayer = new LocalPlayer(packet.name());
        this.client.clientPlayer.setId(packet.id());
        var listener = new ClientLobbyPacketListenerImpl(this.client, this.connection);
        this.connection.setupInboundProtocol(LobbyProtocols.CLIENTBOUND, listener);
        this.connection.sendPacket(LobbyJoinedNotify.INSTANCE);
        this.connection.setupOutboundProtocol(LobbyProtocols.SERVERBOUND);
        this.client.setPanel(new LobbyPanel());
    }

    @Override
    public void handleDisconnect(DisconnectNotify packet) {
        this.connection.disconnect(packet.msg());
    }

    @Override
    public void handleCompression(LoginCompressionNotify packet) {
        this.connection.setupCompression(packet.threshold(), false);
    }

    @Override
    public void handleEnterName(EnterNameReq packet) {
        if (!this.waitingAuthComplete) {
            this.waitingAuthComplete = true;
        }

        var login = new LoginPanel();
        var panel = packet.msg().isEmpty() ? login : new OkPanel(login, "エラー", packet.msg());
        this.client.setPanel(panel);
    }

    @Override
    public void handlePing(PingReq packet) {
        this.connection.sendPacket(new PongRsp(0L));
    }

    @Override
    public void onDisconnect(String msg) {
        var list = new ServerListPanel();
        var panel = msg.isEmpty() ? list : new OkPanel(list, "エラー", msg);
        this.client.getMainWindow().reset();
        this.client.setPanel(panel);
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
