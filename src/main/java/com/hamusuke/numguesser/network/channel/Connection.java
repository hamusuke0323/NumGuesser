package com.hamusuke.numguesser.network.channel;

import com.google.common.base.Suppliers;
import com.google.common.collect.Queues;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.hamusuke.numguesser.network.HandlerNames;
import com.hamusuke.numguesser.network.PacketLogger;
import com.hamusuke.numguesser.network.PacketSendListener;
import com.hamusuke.numguesser.network.encryption.PacketDecryptor;
import com.hamusuke.numguesser.network.encryption.PacketEncryptor;
import com.hamusuke.numguesser.network.listener.PacketListener;
import com.hamusuke.numguesser.network.listener.TickablePacketListener;
import com.hamusuke.numguesser.network.listener.client.ClientboundPacketListener;
import com.hamusuke.numguesser.network.listener.client.info.ClientInfoPacketListener;
import com.hamusuke.numguesser.network.listener.client.login.ClientLoginPacketListener;
import com.hamusuke.numguesser.network.listener.server.ServerboundPacketListener;
import com.hamusuke.numguesser.network.listener.server.handshake.ServerHandshakePacketListener;
import com.hamusuke.numguesser.network.protocol.EmptyPipelineHandler;
import com.hamusuke.numguesser.network.protocol.EmptyPipelineHandler.Inbound;
import com.hamusuke.numguesser.network.protocol.EmptyPipelineHandler.Outbound;
import com.hamusuke.numguesser.network.protocol.PacketDirection;
import com.hamusuke.numguesser.network.protocol.ProtocolInfo;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import com.hamusuke.numguesser.network.protocol.packet.SkipPacketException;
import com.hamusuke.numguesser.network.protocol.packet.disconnect.clientbound.DisconnectNotify;
import com.hamusuke.numguesser.network.protocol.packet.handshake.HandshakeProtocols;
import com.hamusuke.numguesser.network.protocol.packet.handshake.serverbound.ClientIntent;
import com.hamusuke.numguesser.network.protocol.packet.handshake.serverbound.HandshakeReq;
import com.hamusuke.numguesser.network.protocol.packet.info.InfoProtocols;
import com.hamusuke.numguesser.network.protocol.packet.login.LoginProtocols;
import com.hamusuke.numguesser.util.Util;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.flow.FlowControlHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.TimeoutException;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import javax.crypto.Cipher;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.ClosedChannelException;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.RejectedExecutionException;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Connection extends SimpleChannelInboundHandler<Packet<?>> {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final Supplier<NioEventLoopGroup> NETWORK_WORKER_GROUP = Suppliers.memoize(() -> new NioEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Client IO #%d").setDaemon(true).build()));
    public static final Supplier<EpollEventLoopGroup> NETWORK_EPOLL_WORKER_GROUP = Suppliers.memoize(() -> new EpollEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Epoll Client IO #%d").setDaemon(true).build()));
    private static final ProtocolInfo<ServerHandshakePacketListener> INITIAL_PROTOCOL = HandshakeProtocols.SERVERBOUND;
    private final PacketDirection receiving;
    private final Queue<Consumer<Connection>> pendingActions = Queues.newConcurrentLinkedQueue();
    private final PacketLogger packetLogger;
    private Channel channel;
    private SocketAddress address;
    @Nullable
    private volatile PacketListener disconnectListener;
    @Nullable
    private volatile PacketListener packetListener;
    @Nullable
    private String disconnectedReason;
    private boolean disconnectionHandled;
    private boolean handlingFault;
    @Nullable
    private volatile String delayedDisconnect;

    public Connection(PacketDirection receiving, PacketLogger logger) {
        this.receiving = receiving;
        this.packetLogger = logger;
    }

    private static <T extends PacketListener> void handlePacket(Packet<T> packet, PacketListener listener) {
        packet.handle((T) listener);
    }

    private static void syncAfterConfigurationChange(ChannelFuture future) {
        try {
            future.syncUninterruptibly();
        } catch (Exception e) {
            if (!(e instanceof ClosedChannelException)) {
                throw e;
            }

            LOGGER.info("Connection closed during protocol change");
        }
    }

    public static Connection connectToServer(InetSocketAddress address, PacketLogger logger) {
        var connection = new Connection(PacketDirection.CLIENTBOUND, logger);
        var channelfuture = connect(address, true, connection);
        channelfuture.syncUninterruptibly();
        return connection;
    }

    public static ChannelFuture connect(InetSocketAddress address, boolean useNativeTransport, final Connection connection) {
        Class<? extends SocketChannel> oclass;
        EventLoopGroup eventloopgroup;
        if (Epoll.isAvailable() && useNativeTransport) {
            oclass = EpollSocketChannel.class;
            eventloopgroup = NETWORK_EPOLL_WORKER_GROUP.get();
        } else {
            oclass = NioSocketChannel.class;
            eventloopgroup = NETWORK_WORKER_GROUP.get();
        }

        return new Bootstrap().group(eventloopgroup).handler(new ChannelInitializer<>() {
            @Override
            protected void initChannel(Channel c) {
                try {
                    c.config().setOption(ChannelOption.TCP_NODELAY, true);
                } catch (ChannelException ignored) {
                }

                var channelpipeline = c.pipeline().addLast(HandlerNames.TIMEOUT, new ReadTimeoutHandler(30));
                connection.configureSerialization(channelpipeline, PacketDirection.CLIENTBOUND, false);
                connection.configurePacketHandler(channelpipeline);
            }
        }).channel(oclass).connect(address.getAddress(), address.getPort());
    }

    private static String outboundHandlerName(boolean encoder) {
        return encoder ? HandlerNames.ENCODER : HandlerNames.OUTBOUND_CONFIG;
    }

    private static String inboundHandlerName(boolean decoder) {
        return decoder ? HandlerNames.DECODER : HandlerNames.INBOUND_CONFIG;
    }

    private static ChannelOutboundHandler createFrameEncoder(boolean noop) {
        return noop ? new NoOpFrameEncoder() : new PacketPrepender();
    }

    private static ChannelInboundHandler createFrameDecoder(boolean noop) {
        return !noop ? new PacketSplitter() : new NoOpFrameDecoder();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        this.channel = ctx.channel();
        this.address = this.channel.remoteAddress();

        if (this.delayedDisconnect != null) {
            this.disconnect(this.delayedDisconnect);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        this.disconnect("サーバーが停止しました");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (cause instanceof SkipPacketException) {
            LOGGER.debug("Skipping packet due to errors", cause.getCause());
        } else {
            boolean flag = !this.handlingFault;
            this.handlingFault = true;
            if (this.channel.isOpen()) {
                if (cause instanceof TimeoutException) {
                    LOGGER.debug("Timeout", cause);
                    this.disconnect("タイムアウトしました");
                } else {
                    var str = "通信エラーが発生しました。Internal Exception: " + cause;

                    if (flag) {
                        LOGGER.debug("Failed to send packet", cause);
                        if (this.getSending() == PacketDirection.CLIENTBOUND) {
                            this.sendPacket(new DisconnectNotify(str), PacketSendListener.thenRun(() -> this.disconnect(str)));
                        } else {
                            this.disconnect(str);
                        }

                        this.setReadOnly();
                    } else {
                        LOGGER.debug("Double fault", cause);
                        this.disconnect(str);
                    }
                }
            }
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Packet<?> packet) {
        if (this.channel.isOpen()) {
            var packetlistener = this.packetListener;
            if (packetlistener == null) {
                throw new IllegalStateException("Received a packet before the packet listener was initialized");
            }

            if (packetlistener.shouldHandleMessage(packet)) {
                try {
                    handlePacket(packet, packetlistener);
                } catch (RejectedExecutionException e) {
                    this.disconnect("サーバーが停止しました");
                } catch (ClassCastException e) {
                    LOGGER.error("Received {} that couldn't be processed", packet.getClass(), e);
                    this.disconnect("不正なパケットを受信しました");
                }
            }
        }
    }

    public void sendPacket(Packet<?> packet) {
        this.sendPacket(packet, null);
    }

    private void validateListener(ProtocolInfo<?> info, PacketListener listener) {
        Validate.notNull(listener, "packetListener");
        var direction = listener.direction();
        if (direction != this.receiving) {
            var s = String.valueOf(this.receiving);
            throw new IllegalStateException("Trying to set listener for wrong side: connection is " + s + ", but listener is " + direction);
        } else {
            var proto = listener.protocol();
            if (info.id() != proto) {
                var s = String.valueOf(proto);
                throw new IllegalStateException("Listener protocol (" + s + ") does not match requested one " + info);
            }
        }
    }

    public <T extends PacketListener> void setupInboundProtocol(ProtocolInfo<T> info, T listener) {
        this.validateListener(info, listener);
        if (info.direction() != this.getReceiving()) {
            throw new IllegalStateException("Invalid inbound protocol: " + info.id());
        } else {
            this.packetListener = listener;
            this.disconnectListener = null;
            var task = EmptyPipelineHandler.setupInboundProtocol(info, this.packetLogger);

            syncAfterConfigurationChange(this.channel.writeAndFlush(task));
        }
    }

    public void setupOutboundProtocol(ProtocolInfo<?> info) {
        if (info.direction() != this.getSending()) {
            throw new IllegalStateException("Invalid outbound protocol: " + info.id());
        } else {
            var task = EmptyPipelineHandler.setupOutboundProtocol(info, this.packetLogger);
            syncAfterConfigurationChange(this.channel.writeAndFlush(task));
        }
    }

    public void setListenerForServerboundHandshake(PacketListener listener) {
        if (this.packetListener != null) {
            throw new IllegalStateException("Listener already set");
        } else if (this.receiving == PacketDirection.SERVERBOUND && listener.direction() == PacketDirection.SERVERBOUND && listener.protocol() == INITIAL_PROTOCOL.id()) {
            this.packetListener = listener;
        } else {
            throw new IllegalStateException("Invalid initial listener");
        }
    }

    public void initiateServerboundInfoConnection(ClientInfoPacketListener listener) {
        this.initiateServerboundConnection(InfoProtocols.SERVERBOUND, InfoProtocols.CLIENTBOUND, listener, ClientIntent.INFO);
    }

    public void initiateServerboundLoginConnection(ClientLoginPacketListener listener) {
        this.initiateServerboundConnection(LoginProtocols.SERVERBOUND, LoginProtocols.CLIENTBOUND, listener, ClientIntent.LOGIN);
    }

    private <S extends ServerboundPacketListener, C extends ClientboundPacketListener> void initiateServerboundConnection(ProtocolInfo<S> out, ProtocolInfo<C> in, C listener, ClientIntent intention) {
        if (out.id() != in.id()) {
            throw new IllegalStateException("Mismatched initial protocols");
        } else {
            this.disconnectListener = listener;
            this.runOnceConnected(connection -> {
                this.setupInboundProtocol(in, listener);
                connection.sendPacket(new HandshakeReq(intention));
                this.setupOutboundProtocol(out);
            });
        }
    }

    public void sendPacket(Packet<?> packet, @Nullable PacketSendListener listener) {
        this.sendPacket(packet, listener, true);
    }

    public void sendPacket(Packet<?> packet, @Nullable PacketSendListener listener, boolean flush) {
        if (this.isConnected()) {
            this.flushQueue();
            this.sendPacketInternal(packet, listener, flush);
        } else {
            this.pendingActions.add(connection -> connection.sendPacket(packet, listener, flush));
        }
    }

    public void runOnceConnected(Consumer<Connection> consumer) {
        if (this.isConnected()) {
            this.flushQueue();
            consumer.accept(this);
        } else {
            this.pendingActions.add(consumer);
        }
    }

    private void sendPacketInternal(Packet<?> packet, @Nullable PacketSendListener listener, boolean flush) {
        if (this.channel.eventLoop().inEventLoop()) {
            this.doSendPacket(packet, listener, flush);
        } else {
            this.channel.eventLoop().execute(() -> this.doSendPacket(packet, listener, flush));
        }
    }

    private void doSendPacket(Packet<?> packetIn, @Nullable PacketSendListener listener, boolean flush) {
        var channelfuture = flush ? this.channel.writeAndFlush(packetIn) : this.channel.write(packetIn);
        if (listener != null) {
            channelfuture.addListener(future -> {
                if (future.isSuccess()) {
                    listener.onSuccess();
                } else {
                    var packet = listener.onFailure();
                    if (packet != null) {
                        var channelfuture1 = this.channel.writeAndFlush(packet);
                        channelfuture1.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
                    }
                }
            });
        }

        channelfuture.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
    }

    private void flush() {
        if (this.channel.eventLoop().inEventLoop()) {
            this.channel.flush();
        } else {
            this.channel.eventLoop().execute(() -> this.channel.flush());
        }
    }

    private void flushQueue() {
        if (this.channel != null && this.channel.isOpen()) {
            Consumer<Connection> consumer;
            synchronized (this.pendingActions) {
                while ((consumer = this.pendingActions.poll()) != null) {
                    consumer.accept(this);
                }
            }
        }
    }

    public void tick() {
        this.flushQueue();
        if (this.packetListener instanceof TickablePacketListener listener) {
            listener.tick();
        }

        if (!this.isConnected() && !this.disconnectionHandled) {
            this.handleDisconnection();
        }

        if (this.channel != null) {
            this.channel.flush();
        }
    }

    public String getLoggableAddress(boolean show) {
        if (this.address == null) {
            return "local";
        } else {
            return show ? Util.getAddressString(this.address) : "IP hidden";
        }
    }

    public void disconnect(String msg) {
        if (this.channel == null) {
            this.delayedDisconnect = msg;
        }

        if (this.isConnected()) {
            this.channel.close().awaitUninterruptibly();
            this.disconnectedReason = msg;
        }
    }

    public PacketDirection getReceiving() {
        return this.receiving;
    }

    public PacketDirection getSending() {
        return this.receiving.getOpposite();
    }

    public void configurePacketHandler(ChannelPipeline entries) {
        entries.addLast("hackfix", new ChannelOutboundHandlerAdapter() {
            @Override
            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise channelPromise) throws Exception {
                super.write(ctx, msg, channelPromise);
            }
        }).addLast(HandlerNames.PACKET_HANDLER, this);
    }

    public void configureSerialization(ChannelPipeline entries, PacketDirection direction, boolean noop) {
        var opposite = direction.getOpposite();
        boolean flag = direction == PacketDirection.SERVERBOUND;
        boolean flag1 = opposite == PacketDirection.SERVERBOUND;
        entries.addLast(HandlerNames.SPLITTER, createFrameDecoder(noop)).addLast(new FlowControlHandler()).addLast(inboundHandlerName(flag), flag ? new PacketDecoder<>(INITIAL_PROTOCOL, this.packetLogger) : new Inbound()).addLast(HandlerNames.PREPENDER, createFrameEncoder(noop)).addLast(outboundHandlerName(flag1), flag1 ? new PacketEncoder<>(INITIAL_PROTOCOL, this.packetLogger) : new Outbound());
    }

    public void configureInMemoryPipeline(ChannelPipeline entries, PacketDirection direction) {
        this.configureSerialization(entries, direction, true);
    }

    public void setEncryptionKey(Cipher cipher, Cipher cipher1) {
        this.channel.pipeline().addBefore(HandlerNames.SPLITTER, HandlerNames.DECRYPT, new PacketDecryptor(cipher));
        this.channel.pipeline().addBefore(HandlerNames.PREPENDER, HandlerNames.ENCRYPT, new PacketEncryptor(cipher1));
    }

    public boolean isConnected() {
        return this.channel != null && this.channel.isOpen();
    }

    public boolean isConnecting() {
        return this.channel == null;
    }

    public boolean isDisconnected() {
        return this.disconnectionHandled;
    }

    @Nullable
    public PacketListener getPacketListener() {
        return this.packetListener;
    }

    @Nullable
    public String getDisconnectedReason() {
        return this.disconnectedReason;
    }

    public void setReadOnly() {
        if (this.channel != null) {
            this.channel.config().setAutoRead(false);
        }
    }

    public void setupCompression(int threshold, boolean validate) {
        if (threshold >= 0) {
            var handler = this.channel.pipeline().get(HandlerNames.DECOMPRESS);
            if (handler instanceof PacketInflater decoder) {
                decoder.setThreshold(threshold, validate);
            } else {
                this.channel.pipeline().addAfter(HandlerNames.SPLITTER, HandlerNames.DECOMPRESS, new PacketInflater(threshold, validate));
            }

            handler = this.channel.pipeline().get(HandlerNames.COMPRESS);
            if (handler instanceof PacketDeflater encoder) {
                encoder.setThreshold(threshold);
            } else {
                this.channel.pipeline().addAfter(HandlerNames.PREPENDER, HandlerNames.COMPRESS, new PacketDeflater(threshold));
            }
        } else {
            if (this.channel.pipeline().get(HandlerNames.DECOMPRESS) instanceof PacketInflater) {
                this.channel.pipeline().remove(HandlerNames.DECOMPRESS);
            }

            if (this.channel.pipeline().get(HandlerNames.COMPRESS) instanceof PacketDeflater) {
                this.channel.pipeline().remove(HandlerNames.COMPRESS);
            }
        }
    }

    public void handleDisconnection() {
        if (this.channel != null && !this.channel.isOpen()) {
            if (this.disconnectionHandled) {
                LOGGER.warn("handleDisconnection() called twice");
            } else {
                this.disconnectionHandled = true;
                var packetlistener = this.getPacketListener();
                var packetlistener1 = packetlistener != null ? packetlistener : this.disconnectListener;
                if (packetlistener1 != null) {
                    var msg = Objects.requireNonNullElse(this.getDisconnectedReason(), "通信エラーが発生しました");
                    packetlistener1.onDisconnect(msg);
                }
            }
        }
    }
}
