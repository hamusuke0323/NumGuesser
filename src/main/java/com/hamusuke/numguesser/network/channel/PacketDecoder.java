package com.hamusuke.numguesser.network.channel;

import com.hamusuke.numguesser.network.PacketLogger;
import com.hamusuke.numguesser.network.PacketLogger.PacketDetails;
import com.hamusuke.numguesser.network.listener.PacketListener;
import com.hamusuke.numguesser.network.protocol.ProtocolInfo;
import com.hamusuke.numguesser.network.protocol.TerminalPacketHandlers;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.io.IOException;
import java.util.List;

public class PacketDecoder<T extends PacketListener> extends ByteToMessageDecoder {
    private final ProtocolInfo<T> protocolInfo;
    private final PacketLogger logger;

    public PacketDecoder(ProtocolInfo<T> protocolInfo, PacketLogger logger) {
        this.protocolInfo = protocolInfo;
        this.logger = logger;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int size = in.readableBytes();
        if (size != 0) {
            var packet = this.protocolInfo.codec().decode(in);
            var type = packet.type();
            if (in.readableBytes() > 0) {
                var s = this.protocolInfo.id().id();
                throw new IOException("Packet " + s + "/" + type + " (" + packet.getClass().getSimpleName() + ") was larger than I expected, found " + in.readableBytes() + " bytes extra whilst reading packet " + type);
            } else {
                out.add(packet);
                TerminalPacketHandlers.handleInboundTerminalPacket(ctx, packet);
                this.logger.receive(new PacketDetails(packet, size));
            }
        }
    }
}
