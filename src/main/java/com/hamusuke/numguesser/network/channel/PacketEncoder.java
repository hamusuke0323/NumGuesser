package com.hamusuke.numguesser.network.channel;

import com.hamusuke.numguesser.network.PacketLogger;
import com.hamusuke.numguesser.network.PacketLogger.PacketDetails;
import com.hamusuke.numguesser.network.listener.PacketListener;
import com.hamusuke.numguesser.network.protocol.ProtocolInfo;
import com.hamusuke.numguesser.network.protocol.TerminalPacketHandlers;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import com.hamusuke.numguesser.network.protocol.packet.SkipPacketException;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PacketEncoder<T extends PacketListener> extends MessageToByteEncoder<Packet<T>> {
    private static final Logger LOGGER = LogManager.getLogger();
    private final ProtocolInfo<T> protocolInfo;
    private final PacketLogger logger;

    public PacketEncoder(ProtocolInfo<T> protocolInfo, PacketLogger logger) {
        this.protocolInfo = protocolInfo;
        this.logger = logger;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Packet<T> msg, ByteBuf out) {
        var type = msg.type();

        try {
            this.protocolInfo.codec().encode(out, msg);
            int i = out.readableBytes();
            this.logger.send(new PacketDetails(msg, i));
        } catch (Throwable e) {
            LOGGER.error("Error sending packet {}", type, e);
            if (msg.isSkippable()) {
                throw new SkipPacketException(e);
            }

            throw e;
        } finally {
            TerminalPacketHandlers.handleOutboundTerminalPacket(ctx, msg);
        }
    }
}
