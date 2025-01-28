package com.hamusuke.numguesser.network.protocol;

import com.hamusuke.numguesser.network.HandlerNames;
import com.hamusuke.numguesser.network.protocol.EmptyPipelineHandler.Inbound;
import com.hamusuke.numguesser.network.protocol.EmptyPipelineHandler.Outbound;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import io.netty.channel.ChannelHandlerContext;

public interface TerminalPacketHandlers {
    static void handleInboundTerminalPacket(ChannelHandlerContext ctx, Packet<?> packet) {
        if (packet.isTerminal()) {
            ctx.channel().config().setAutoRead(false);
            ctx.pipeline().addBefore(ctx.name(), HandlerNames.INBOUND_CONFIG, new Inbound());
            ctx.pipeline().remove(ctx.name());
        }
    }

    static void handleOutboundTerminalPacket(ChannelHandlerContext ctx, Packet<?> packet) {
        if (packet.isTerminal()) {
            ctx.pipeline().addAfter(ctx.name(), HandlerNames.OUTBOUND_CONFIG, new Outbound());
            ctx.pipeline().remove(ctx.name());
        }
    }
}
