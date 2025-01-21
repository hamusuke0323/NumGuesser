package com.hamusuke.numguesser.client;

import com.formdev.flatlaf.FlatDarkLaf;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.hamusuke.numguesser.Constants;
import com.hamusuke.numguesser.client.config.Config;
import com.hamusuke.numguesser.client.gui.MainWindow;
import com.hamusuke.numguesser.client.gui.component.panel.MainMenuPanel;
import com.hamusuke.numguesser.client.gui.component.panel.Panel;
import com.hamusuke.numguesser.client.gui.component.panel.ServerListPanel;
import com.hamusuke.numguesser.client.gui.component.table.PacketLogTable;
import com.hamusuke.numguesser.client.gui.component.table.PlayerTable;
import com.hamusuke.numguesser.client.network.Chat;
import com.hamusuke.numguesser.client.network.ClientPacketLogger;
import com.hamusuke.numguesser.client.network.listener.info.ClientInfoPacketListenerImpl;
import com.hamusuke.numguesser.client.network.listener.login.ClientLoginPacketListenerImpl;
import com.hamusuke.numguesser.client.network.listener.main.ClientCommonPacketListenerImpl;
import com.hamusuke.numguesser.client.network.player.LocalPlayer;
import com.hamusuke.numguesser.client.room.ClientRoom;
import com.hamusuke.numguesser.network.ServerInfo;
import com.hamusuke.numguesser.network.ServerInfo.Status;
import com.hamusuke.numguesser.network.channel.Connection;
import com.hamusuke.numguesser.network.listener.client.lobby.ClientLobbyPacketListener;
import com.hamusuke.numguesser.network.protocol.Protocol;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import com.hamusuke.numguesser.network.protocol.packet.serverbound.common.DisconnectReq;
import com.hamusuke.numguesser.network.protocol.packet.serverbound.handshake.HandshakeReq;
import com.hamusuke.numguesser.network.protocol.packet.serverbound.lobby.LobbyDisconnectReq;
import com.hamusuke.numguesser.network.protocol.packet.serverbound.login.KeyExchangeReq;
import com.hamusuke.numguesser.util.Util;
import com.hamusuke.numguesser.util.thread.ReentrantThreadExecutor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import javax.swing.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import static com.hamusuke.numguesser.util.PacketUtil.LOOP_PACKETS;

public final class NumGuesser extends ReentrantThreadExecutor<Runnable> {
    private static final Logger LOGGER = LogManager.getLogger();
    private static NumGuesser INSTANCE;
    private static final Gson GSON = new Gson();
    private final AtomicBoolean running = new AtomicBoolean();
    private final TickCounter tickCounter = new TickCounter(20.0F, 0L);
    @Nullable
    private Connection connection;
    @Nullable
    public ClientCommonPacketListenerImpl listener;
    private final MainWindow mainWindow;
    @Nullable
    public LocalPlayer clientPlayer;
    private Thread thread;
    private int tickCount;
    public PlayerTable playerTable;
    public Chat chat;
    public final PacketLogTable packetLogTable = new PacketLogTable();
    @Nullable
    public ClientRoom curRoom;
    private final File serversFile;
    public final Config config;
    private final List<ServerInfo> servers = Collections.synchronizedList(Lists.newArrayList());
    private final List<Connection> infoConnections = Collections.synchronizedList(Lists.newArrayList());

    NumGuesser() {
        super("Client");

        if (INSTANCE != null) {
            throw new IllegalStateException("NumGuesser is singleton class!");
        }

        INSTANCE = this;
        this.running.set(true);
        this.thread = Thread.currentThread();
        this.serversFile = new File("./servers.json");
        this.loadServers();
        this.config = new Config("./config.json");
        this.config.loadConfig();

        if (this.config.darkTheme.getValue()) {
            FlatDarkLaf.setup();
        }

        this.mainWindow = new MainWindow(this);
        this.mainWindow.updateUI();
        this.mainWindow.setPanel(new MainMenuPanel());
        this.mainWindow.setSize(1280, 720);
        this.mainWindow.setLocationRelativeTo(null);
        this.mainWindow.setVisible(true);
    }

    public static NumGuesser getInstance() {
        return INSTANCE;
    }

    private synchronized void loadServers() {
        this.servers.clear();

        try {
            if (!this.serversFile.exists() || !this.serversFile.isFile()) {
                this.serversFile.createNewFile();
            }
        } catch (Exception e) {
            LOGGER.warn("Failed to load servers", e);
            return;
        }

        if (this.serversFile.exists() && this.serversFile.isFile()) {
            try (var isr = new InputStreamReader(new FileInputStream(this.serversFile), StandardCharsets.UTF_8)) {
                var servers = GSON.fromJson(isr, JsonArray.class);
                if (servers == null) {
                    return;
                }

                Set<ServerInfo> set = Sets.newHashSet();
                servers.forEach(e -> {
                    try {
                        var info = GSON.fromJson(e, ServerInfo.class);
                        if (info == null) {
                            return;
                        }

                        info.status = Status.NONE;
                        set.add(info);
                    } catch (Exception ex) {
                        LOGGER.warn("Error occurred while loading json and skip", ex);
                    }
                });

                this.servers.addAll(set);
            } catch (Exception e) {
                LOGGER.warn("Failed to load servers json file", e);
            }
        }
    }

    public synchronized void saveServers() {
        try (var w = new OutputStreamWriter(new FileOutputStream(this.serversFile), StandardCharsets.UTF_8)) {
            GSON.toJson(this.servers, w);
            w.flush();
        } catch (Exception e) {
            LOGGER.warn("Error occurred while saving servers", e);
        }
    }

    public List<ServerInfo> getServers() {
        return ImmutableList.copyOf(this.servers);
    }

    public boolean addServer(ServerInfo info) {
        if (this.servers.contains(info)) {
            return false;
        }

        this.servers.add(info);
        return true;
    }

    public void removeServer(ServerInfo info) {
        this.servers.remove(info);
    }

    public void checkServerInfo(ServerInfo info) {
        CompletableFuture.runAsync(() -> {
            info.status = Status.CONNECTING;
            this.onServerInfoChanged();
            var address = new InetSocketAddress(info.address, info.port);
            var connection = Connection.connect(new ClientPacketLogger(this), address);
            connection.setListener(new ClientInfoPacketListenerImpl(this, connection, info));
            connection.sendPacket(new HandshakeReq(Protocol.INFO));
            this.infoConnections.add(connection);
        }, this).exceptionally(throwable -> {
            info.status = Status.FAILED;
            this.onServerInfoChanged();
            return null;
        });
    }

    public void onServerInfoChanged() {
        if (this.getPanel() instanceof ServerListPanel panel) {
            panel.onServerInfoChanged();
        }
    }

    public void run() {
        this.thread = Thread.currentThread();
        if (Runtime.getRuntime().availableProcessors() > 4) {
            this.thread.setPriority(10);
        }

        try {
            boolean bl = false;

            while (this.running.get()) {
                try {
                    this.loop(!bl);
                } catch (OutOfMemoryError e) {
                    if (bl) {
                        throw e;
                    }

                    System.gc();
                    LOGGER.fatal("Out of memory", e);
                    bl = true;
                }
            }
        } catch (Exception e) {
            LOGGER.fatal("Error thrown!", e);
        } finally {
            this.stop();
        }
    }

    public String getGameTitle() {
        return "NumGuesser " + Constants.VERSION;
    }

    public void setWindowTitle(String title) {
        this.mainWindow.setTitle(title);
    }

    public MainWindow getMainWindow() {
        return this.mainWindow;
    }

    public Panel getPanel() {
        return this.mainWindow.getPanel();
    }

    public void setPanel(Panel panel) {
        this.mainWindow.setPanel(panel);
    }

    public void stopLooping() {
        if (this.connection != null) {
            this.connection.disconnect("Client Exit");
        }

        this.running.set(false);
    }

    public void stop() {
        try {
            LOGGER.info("Stopping");
            this.close();
        } catch (Exception e) {
            LOGGER.warn("Error occurred while stopping", e);
        }
    }

    private void loop(boolean tick) {
        if (tick) {
            int i = this.tickCounter.beginLoopTick(Util.getMeasuringTimeMs());
            this.runTasks();
            for (int j = 0; j < Math.min(10, i); j++) {
                this.tick();
            }
        }
    }

    public void tick() {
        this.tickCount++;

        SwingUtilities.invokeLater(this.mainWindow::tick);
        if (this.connection != null) {
            this.connection.tick();
            if (this.connection.isDisconnected()) {
                this.connection = null;
            }
        }

        this.infoConnections.forEach(Connection::tick);
        this.infoConnections.removeIf(Connection::isDisconnected);
    }

    public void connectToServer(String host, int port, Consumer<String> consumer) {
        this.clientPlayer = null;
        var address = new InetSocketAddress(host, port);
        this.connection = Connection.connect(new ClientPacketLogger(this), address);
        this.connection.setListener(new ClientLoginPacketListenerImpl(this.connection, this, consumer));
        this.connection.sendPacket(new HandshakeReq(Protocol.LOGIN));
        this.connection.sendPacket(new KeyExchangeReq());
    }

    public boolean isPacketTrash(Packet<?> packet) {
        return LOOP_PACKETS.contains(packet.getClass().getSimpleName());
    }

    @Override
    public void close() {
        System.exit(0);
    }

    @Override
    protected Runnable createTask(Runnable runnable) {
        return runnable;
    }

    @Override
    protected boolean canExecute(Runnable task) {
        return true;
    }

    @Override
    protected Thread getThread() {
        return this.thread;
    }

    @Nullable
    public Connection getConnection() {
        return this.connection;
    }

    public void disconnect() {
        if (this.connection == null) {
            return;
        }

        if (this.connection.getPacketListener() instanceof ClientLobbyPacketListener) {
            this.connection.sendPacket(new LobbyDisconnectReq(), future -> this.connection.disconnect(""));
            return;
        }

        this.connection.sendPacket(new DisconnectReq(), future -> this.connection.disconnect(""));
    }
}
