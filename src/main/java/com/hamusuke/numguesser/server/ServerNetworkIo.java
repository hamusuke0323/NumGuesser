package com.hamusuke.numguesser.server;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.hamusuke.numguesser.network.HandlerNames;
import com.hamusuke.numguesser.network.PacketLogger;
import com.hamusuke.numguesser.network.PacketSendListener;
import com.hamusuke.numguesser.network.channel.Connection;
import com.hamusuke.numguesser.network.protocol.PacketDirection;
import com.hamusuke.numguesser.network.protocol.packet.disconnect.clientbound.DisconnectNotify;
import com.hamusuke.numguesser.server.network.listener.handshake.ServerHandshakePacketListenerImpl;
import com.hamusuke.numguesser.util.Lazy;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.net.InetAddress;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class ServerNetworkIo {
    public static final Lazy<NioEventLoopGroup> DEFAULT_CHANNEL = new Lazy<>(() -> {
        return new NioEventLoopGroup(0, (new ThreadFactoryBuilder()).setNameFormat("Netty Server IO #%d").setDaemon(true).build());
    });
    public static final Lazy<EpollEventLoopGroup> EPOLL_CHANNEL = new Lazy<>(() -> {
        return new EpollEventLoopGroup(0, (new ThreadFactoryBuilder()).setNameFormat("Netty Epoll Server IO #%d").setDaemon(true).build());
    });
    private static final Logger LOGGER = LogManager.getLogger();
    public final AtomicBoolean active = new AtomicBoolean();
    final NumGuesserServer server;
    final List<Connection> connections = Collections.synchronizedList(Lists.newArrayList());
    private final List<ChannelFuture> channels = Collections.synchronizedList(Lists.newArrayList());

    public ServerNetworkIo(NumGuesserServer server) {
        this.server = server;
        this.active.set(true);
    }

    public void bind(@Nullable InetAddress address, int port) {
        synchronized (this.channels) {
            Class<? extends ServerChannel> clazz;
            Lazy<? extends EventLoopGroup> lazy;
            if (Epoll.isAvailable()) {
                clazz = EpollServerSocketChannel.class;
                lazy = EPOLL_CHANNEL;
                LOGGER.info("Using epoll channel type");
            } else {
                clazz = NioServerSocketChannel.class;
                lazy = DEFAULT_CHANNEL;
                LOGGER.info("Using default channel type");
            }

            this.channels.add(new ServerBootstrap().channel(clazz).childHandler(new ChannelInitializer<>() {
                @Override
                protected void initChannel(Channel channel) {
                    try {
                        channel.config().setOption(ChannelOption.TCP_NODELAY, true);
                    } catch (ChannelException ignored) {
                    }

                    var pipeline = channel.pipeline().addLast(HandlerNames.TIMEOUT, new ReadTimeoutHandler(30));
                    var connection = new Connection(PacketDirection.SERVERBOUND, PacketLogger.EMPTY);
                    connection.configureSerialization(pipeline, PacketDirection.SERVERBOUND, false);
                    ServerNetworkIo.this.connections.add(connection);
                    connection.configurePacketHandler(pipeline);
                    connection.setListenerForServerboundHandshake(new ServerHandshakePacketListenerImpl(ServerNetworkIo.this.server, connection));
                }
            }).group(lazy.get()).localAddress(address, port).bind().syncUninterruptibly());
        }
    }

    public void stop() {
        this.active.set(false);

        for (var channelFuture : this.channels) {
            try {
                channelFuture.channel().close().sync();
            } catch (InterruptedException e) {
                LOGGER.error("Interrupted while closing channel");
            }
        }
    }

    public void tick() {
        synchronized (this.connections) {
            for (var connection : this.connections) {
                if (connection.isConnected()) {
                    try {
                        connection.tick();
                    } catch (Exception e) {
                        LOGGER.warn("Failed to handle packet for " + connection.getLoggableAddress(true), e);
                        var msg = "パケットの処理に失敗しました\n" + e;
                        connection.sendPacket(new DisconnectNotify(msg), PacketSendListener.thenRun(() -> connection.disconnect(msg)));
                        connection.setReadOnly();
                    }
                }
            }

            this.connections.removeIf(connection -> {
                if (!connection.isConnecting() && !connection.isConnected()) {
                    connection.handleDisconnection();
                    return true;
                }

                return false;
            });
        }
    }

    public NumGuesserServer getServer() {
        return this.server;
    }
}
