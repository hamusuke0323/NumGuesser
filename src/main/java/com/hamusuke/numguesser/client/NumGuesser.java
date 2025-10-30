package com.hamusuke.numguesser.client;

import com.formdev.flatlaf.FlatDarkLaf;
import com.hamusuke.numguesser.client.config.Config;
import com.hamusuke.numguesser.client.event.ClientEventBus;
import com.hamusuke.numguesser.client.event.UINotifier;
import com.hamusuke.numguesser.client.gui.MainWindow;
import com.hamusuke.numguesser.client.gui.component.panel.Panel;
import com.hamusuke.numguesser.client.gui.component.panel.menu.MainMenuPanel;
import com.hamusuke.numguesser.client.gui.component.table.PacketLogTable;
import com.hamusuke.numguesser.client.gui.component.table.PlayerTable;
import com.hamusuke.numguesser.client.network.Chat;
import com.hamusuke.numguesser.client.network.ClientPacketLogger;
import com.hamusuke.numguesser.client.network.ServerChecker;
import com.hamusuke.numguesser.client.network.ServerInfoRegistry;
import com.hamusuke.numguesser.client.network.listener.login.ClientLoginPacketListenerImpl;
import com.hamusuke.numguesser.client.network.listener.main.ClientCommonPacketListenerImpl;
import com.hamusuke.numguesser.client.network.player.LocalPlayer;
import com.hamusuke.numguesser.client.room.ClientRoom;
import com.hamusuke.numguesser.network.PacketSendListener;
import com.hamusuke.numguesser.network.channel.Connection;
import com.hamusuke.numguesser.network.protocol.packet.disconnect.serverbound.DisconnectReq;
import com.hamusuke.numguesser.network.protocol.packet.login.serverbound.KeyExchangeReq;
import com.hamusuke.numguesser.util.Util;
import com.hamusuke.numguesser.util.thread.ReentrantThreadExecutor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import javax.swing.*;
import java.io.File;
import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public final class NumGuesser extends ReentrantThreadExecutor<Runnable> {
    private static final Logger LOGGER = LogManager.getLogger();
    private static NumGuesser INSTANCE;
    private final AtomicBoolean running = new AtomicBoolean();
    private final TickCounter tickCounter = new TickCounter(20.0F, 0L);
    public final Config config;
    public final PacketLogTable packetLogTable = new PacketLogTable();
    public final ClientEventBus eventBus = new ClientEventBus();
    private final MainWindow mainWindow;
    private final ServerInfoRegistry serverInfoRegistry;
    private final ServerChecker serverChecker;
    @Nullable
    private Connection connection;
    @Nullable
    public ClientCommonPacketListenerImpl listener;
    @Nullable
    public LocalPlayer clientPlayer;
    private Thread thread;
    public PlayerTable playerTable;
    public Chat chat;
    @Nullable
    public ClientRoom curRoom;

    NumGuesser() {
        super("Client");

        if (INSTANCE != null) {
            throw new IllegalStateException("NumGuesser is singleton class!");
        }

        INSTANCE = this;
        this.running.set(true);
        this.thread = Thread.currentThread();
        this.serverInfoRegistry = new ServerInfoRegistry(new File("./servers.json"));
        this.serverChecker = new ServerChecker();
        this.config = new Config("./config.json");
        if (this.config.darkTheme.getValue()) {
            FlatDarkLaf.setup();
        }

        this.mainWindow = new MainWindow(this);
        this.mainWindow.updateUI();
        this.mainWindow.setPanel(new MainMenuPanel());
        this.mainWindow.setSize(1280, 720);
        this.mainWindow.setLocationRelativeTo(null);
        this.mainWindow.setVisible(true);
        this.eventBus.register(new UINotifier());
    }

    public static NumGuesser getInstance() {
        if (INSTANCE == null) {
            throw new IllegalStateException("NumGuesser is not initialized!");
        }

        return INSTANCE;
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

    private void loop(boolean tick) {
        if (tick) {
            int i = this.tickCounter.beginLoopTick(Util.getMeasuringTimeMs());
            this.runTasks();
            for (int j = 0; j < Math.min(10, i); j++) {
                this.tick();
            }
        }
    }

    private void tick() {
        SwingUtilities.invokeLater(this.mainWindow::tick);
        if (this.connection != null) {
            this.connection.tick();
            if (this.connection.isDisconnected()) {
                this.connection = null;
            }
        } else {
            this.serverChecker.tick();
        }
    }

    public void stopLooping() {
        if (this.connection != null) {
            this.connection.disconnect("Client Exit");
        }

        this.running.set(false);
    }

    private void stop() {
        try {
            LOGGER.info("Stopping");
            this.close();
        } catch (Exception e) {
            LOGGER.warn("Error occurred while stopping", e);
        }
    }

    @Override
    public void close() {
        System.exit(0);
    }

    public ServerInfoRegistry getServerInfoRegistry() {
        return this.serverInfoRegistry;
    }

    public ServerChecker getServerChecker() {
        return this.serverChecker;
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

    public void connectToServer(String host, int port, Consumer<String> consumer) {
        this.clientPlayer = null;
        var address = new InetSocketAddress(host, port);
        this.connection = Connection.connectToServer(address, new ClientPacketLogger(this));
        this.connection.initiateServerboundLoginConnection(new ClientLoginPacketListenerImpl(this.connection, this, consumer));
        this.connection.sendPacket(KeyExchangeReq.INSTANCE);
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

        this.connection.sendPacket(DisconnectReq.INSTANCE, PacketSendListener.thenRun(() -> this.connection.disconnect("")));
    }
}
