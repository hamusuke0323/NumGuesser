package com.hamusuke.numguesser.network.protocol;

import com.hamusuke.numguesser.network.HandlerNames;
import com.hamusuke.numguesser.network.PacketLogger;
import com.hamusuke.numguesser.network.channel.PacketDecoder;
import com.hamusuke.numguesser.network.channel.PacketEncoder;
import com.hamusuke.numguesser.network.listener.PacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import io.netty.util.ReferenceCountUtil;

public class EmptyPipelineHandler {
    public static <T extends PacketListener> InboundConfigTask setupInboundProtocol(ProtocolInfo<T> info, PacketLogger logger) {
        return setupInboundHandler(new PacketDecoder<>(info, logger));
    }

    private static InboundConfigTask setupInboundHandler(ChannelInboundHandler handler) {
        return ctx -> {
            ctx.pipeline().replace(ctx.name(), HandlerNames.DECODER, handler);
            ctx.channel().config().setAutoRead(true);
        };
    }

    public static <T extends PacketListener> OutboundConfigTask setupOutboundProtocol(ProtocolInfo<T> info, PacketLogger logger) {
        return setupOutboundHandler(new PacketEncoder<>(info, logger));
    }

    private static OutboundConfigTask setupOutboundHandler(ChannelOutboundHandler handler) {
        return ctx -> ctx.pipeline().replace(ctx.name(), HandlerNames.ENCODER, handler);
    }

    public interface InboundConfigTask {
        void run(ChannelHandlerContext ctx);

        default InboundConfigTask andThen(InboundConfigTask task) {
            return ctx -> {
                this.run(ctx);
                task.run(ctx);
            };
        }
    }

    public interface OutboundConfigTask {
        void run(ChannelHandlerContext ctx);

        default OutboundConfigTask andThen(OutboundConfigTask task) {
            return ctx -> {
                this.run(ctx);
                task.run(ctx);
            };
        }
    }

    public static class Inbound extends ChannelDuplexHandler {
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) {
            if (!(msg instanceof ByteBuf) && !(msg instanceof Packet<?>)) {
                ctx.fireChannelRead(msg);
            } else {
                ReferenceCountUtil.release(msg);
                throw new DecoderException("Pipeline has no inbound protocol configured, can't process packet " + msg);
            }
        }

        @Override
        public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise channelPromise) throws Exception {
            if (msg instanceof InboundConfigTask task) {
                try {
                    task.run(ctx);
                } finally {
                    ReferenceCountUtil.release(msg);
                }

                channelPromise.setSuccess();
            } else {
                ctx.write(msg, channelPromise);
            }
        }
    }

    public static class Outbound extends ChannelOutboundHandlerAdapter {
        @Override
        public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise channelPromise) throws Exception {
            if (msg instanceof Packet<?>) {
                ReferenceCountUtil.release(msg);
                throw new EncoderException("Pipeline has no outbound protocol configured, can't process packet " + msg);
            } else {
                if (msg instanceof OutboundConfigTask task) {
                    try {
                        task.run(ctx);
                    } finally {
                        ReferenceCountUtil.release(msg);
                    }

                    channelPromise.setSuccess();
                } else {
                    ctx.write(msg, channelPromise);
                }
            }
        }
    }
}
