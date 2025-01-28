package com.hamusuke.numguesser.server.network.listener.login;

import com.hamusuke.numguesser.network.PacketSendListener;
import com.hamusuke.numguesser.network.channel.Connection;
import com.hamusuke.numguesser.network.encryption.NetworkEncryptionUtil;
import com.hamusuke.numguesser.network.listener.TickablePacketListener;
import com.hamusuke.numguesser.network.listener.server.login.ServerLoginPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.lobby.LobbyProtocols;
import com.hamusuke.numguesser.network.protocol.packet.login.clientbound.*;
import com.hamusuke.numguesser.network.protocol.packet.login.serverbound.*;
import com.hamusuke.numguesser.server.NumGuesserServer;
import com.hamusuke.numguesser.server.network.ServerPlayer;
import com.hamusuke.numguesser.server.network.listener.lobby.ServerLobbyPacketListenerImpl;
import com.mojang.brigadier.StringReader;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.Random;
import java.util.function.Function;

public class ServerLoginPacketListenerImpl implements ServerLoginPacketListener, TickablePacketListener {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final int TIMEOUT_TICKS = 600;
    private static final int ENCRYPTION_WAIT_TICKS = 60;
    private static final Random RANDOM = new Random();
    public final Connection connection;
    final NumGuesserServer server;
    private final byte[] nonce = new byte[4];
    State state;
    private int ticks;
    private int encWaitTicks;
    private ServerPlayer serverPlayer;

    public ServerLoginPacketListenerImpl(NumGuesserServer server, Connection connection) {
        this.state = State.KEY_EX;
        this.server = server;
        this.connection = connection;
        RANDOM.nextBytes(this.nonce);
    }

    public static boolean isValidName(String name) {
        return name.chars().filter(value -> !StringReader.isAllowedInUnquotedString((char) value)).findAny().isEmpty();
    }

    @Override
    public void tick() {
        if (this.state == State.ENTER_NAME && this.encWaitTicks > 0) {
            this.encWaitTicks--;
            if (this.encWaitTicks <= 0) {
                this.connection.sendPacket(new EnterNameReq());
            }
        } else if (this.state == State.READY) {
            this.acceptPlayer();
        }

        this.ticks++;
        if ((this.state == State.KEY_EX || this.state == State.ENCRYPTION) && this.ticks == TIMEOUT_TICKS) {
            LOGGER.info("Login is too slow");
            this.disconnect("Login is too slow");
        }
    }

    @Override
    public Connection getConnection() {
        return this.connection;
    }

    private void disconnect() {
        this.disconnect("");
    }

    private void disconnect(String msg) {
        try {
            LOGGER.info("Disconnecting {}", this.getConnectionInfo());
            this.connection.sendPacket(new LoginDisconnectNotify(msg));
            this.connection.disconnect(msg);
        } catch (Exception e) {
            LOGGER.error("Error while disconnecting player", e);
        }
    }

    public void acceptPlayer() {
        if (this.state == State.PROTOCOL_SWITCHING) {
            return;
        }

        this.state = State.ACCEPTED;
        if (this.server.getCompressionThreshold() >= 0) {
            this.connection.sendPacket(new LoginCompressionNotify(this.server.getCompressionThreshold()), PacketSendListener.thenRun(() -> {
                this.connection.setupCompression(this.server.getCompressionThreshold(), true);
            }));
        }

        if (this.server.getPlayerManager().canJoin(this.serverPlayer)) {
            this.state = State.PROTOCOL_SWITCHING;
            this.connection.sendPacket(new LoginSuccessNotify(this.serverPlayer));
        } else {
            this.disconnect();
        }
    }

    @Override
    public void onDisconnect(String msg) {
        LOGGER.info("{} lost connection", this.getConnectionInfo());
    }

    @Override
    public boolean isAcceptingMessages() {
        return this.connection.isConnected();
    }

    public String getConnectionInfo() {
        return String.valueOf(this.connection.getLoggableAddress(true));
    }

    @Override
    public void handleKeyEx(KeyExchangeReq packet) {
        Validate.validState(this.state == State.KEY_EX, "Unexpected key exchange packet");
        if (this.state != State.KEY_EX) {
            this.disconnect();
            return;
        }

        if (this.server.getKeyPair() == null) {
            this.disconnect("Internal server error");
            return;
        }

        this.state = State.ENCRYPTION;
        this.connection.sendPacket(new KeyExchangeRsp(this.server.getKeyPair().getPublic().getEncoded(), this.nonce));
    }

    @Override
    public void handleEncryption(EncryptionSetupReq packet) {
        Validate.validState(this.state == State.ENCRYPTION, "Unexpected encryption packet");

        if (this.server.getKeyPair() == null) {
            this.disconnect("Internal server error");
            return;
        }

        var privateKey = this.server.getKeyPair().getPrivate();

        try {
            if (!Arrays.equals(this.nonce, packet.decryptNonce(privateKey))) {
                throw new IllegalStateException("Protocol error");
            }

            var secretKey = packet.decryptSecretKey(privateKey);
            var cipher = NetworkEncryptionUtil.cipherFromKey(2, secretKey);
            var cipher2 = NetworkEncryptionUtil.cipherFromKey(1, secretKey);
            this.connection.setEncryptionKey(cipher, cipher2);
            this.state = State.ENTER_NAME;
            this.encWaitTicks = ENCRYPTION_WAIT_TICKS;
        } catch (Exception e) {
            throw new IllegalStateException("Protocol error", e);
        }
    }

    @Override
    public void handlePing(AliveReq packet) {
        this.connection.sendPacket(AliveRsp.INSTANCE);
    }

    @Override
    public void handleEnterName(EnterNameRsp packet) {
        Validate.validState(this.state == State.ENTER_NAME, "Unexpected enter name packet");

        var res = this.tryLogin(packet.name());
        switch (res) {
            case OK -> {
                this.serverPlayer = new ServerPlayer(packet.name(), this.server);
                this.serverPlayer.setAuthorized(true);
                this.state = State.READY;
            }
            case DUPLICATED_NAME, INVALID_CHARS_IN_NAME ->
                    this.connection.sendPacket(new EnterNameReq(res.messageFactory.apply(packet.name())));
        }
    }

    @Override
    public void handleLobbyJoined(LobbyJoinedNotify packet) {
        Validate.validState(this.state == State.PROTOCOL_SWITCHING, "Unexpected lobby joined packet");

        this.connection.setupOutboundProtocol(LobbyProtocols.CLIENTBOUND);
        new ServerLobbyPacketListenerImpl(this.server, this.connection, this.serverPlayer);
        this.server.getPlayerManager().addPlayer(this.serverPlayer);
    }

    private LoginResult tryLogin(String name) {
        if (!isValidName(name)) {
            return LoginResult.INVALID_CHARS_IN_NAME;
        }

        if (this.server.getPlayerManager().getPlayers().stream().anyMatch(player -> player.getName().equals(name))) {
            return LoginResult.DUPLICATED_NAME;
        }

        return LoginResult.OK;
    }

    private enum State {
        KEY_EX,
        ENCRYPTION,
        ENTER_NAME,
        READY,
        ACCEPTED,
        PROTOCOL_SWITCHING,
    }

    private enum LoginResult {
        OK(Function.identity()),
        INVALID_CHARS_IN_NAME(s -> "使用不可能な文字が含まれています"),
        DUPLICATED_NAME(s -> String.format("'%s' という名前は既に使われています。別の名前を使用してください", s));

        private final Function<String, String> messageFactory;

        LoginResult(Function<String, String> messageFactory) {
            this.messageFactory = messageFactory;
        }
    }
}
