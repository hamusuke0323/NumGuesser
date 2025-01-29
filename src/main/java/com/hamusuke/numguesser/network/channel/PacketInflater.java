package com.hamusuke.numguesser.network.channel;

import com.hamusuke.numguesser.network.VarInt;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.DecoderException;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

public class PacketInflater extends ByteToMessageDecoder {
    public static final int MAXIMUM_COMPRESSED_LENGTH = 2097152;
    public static final int MAXIMUM_UNCOMPRESSED_LENGTH = 8388608;
    private final Inflater inflater;
    private int threshold;
    private boolean validateDecompressed;

    public PacketInflater(int threshold, boolean validate) {
        this.threshold = threshold;
        this.validateDecompressed = validate;
        this.inflater = new Inflater();
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() != 0) {
            int i = VarInt.read(in);
            if (i == 0) {
                out.add(in.readBytes(in.readableBytes()));
            } else {
                if (this.validateDecompressed) {
                    if (i < this.threshold) {
                        throw new DecoderException("Badly compressed packet - size of " + i + " is below server threshold of " + this.threshold);
                    }

                    if (i > MAXIMUM_UNCOMPRESSED_LENGTH) {
                        throw new DecoderException("Badly compressed packet - size of " + i + " is larger than protocol maximum of " + MAXIMUM_UNCOMPRESSED_LENGTH);
                    }
                }

                this.setupInflaterInput(in);
                var buf = this.inflate(ctx, i);
                this.inflater.reset();
                out.add(buf);
            }
        }
    }

    private void setupInflaterInput(ByteBuf buf) {
        ByteBuffer buffer;
        if (buf.nioBufferCount() > 0) {
            buffer = buf.nioBuffer();
            buf.skipBytes(buf.readableBytes());
        } else {
            buffer = ByteBuffer.allocateDirect(buf.readableBytes());
            buf.readBytes(buffer);
            buffer.flip();
        }

        this.inflater.setInput(buffer);
    }

    private ByteBuf inflate(ChannelHandlerContext ctx, int i) throws DataFormatException {
        var buf = ctx.alloc().directBuffer(i);

        try {
            var buffer = buf.internalNioBuffer(0, i);
            int pos = buffer.position();
            this.inflater.inflate(buffer);
            int diff = buffer.position() - pos;
            if (diff != i) {
                throw new DecoderException("Badly compressed packet - actual length of uncompressed payload " + diff + " is does not match declared size " + i);
            } else {
                buf.writerIndex(buf.writerIndex() + diff);
                return buf;
            }
        } catch (Exception e) {
            buf.release();
            throw e;
        }
    }

    public void setThreshold(int threshold, boolean validate) {
        this.threshold = threshold;
        this.validateDecompressed = validate;
    }
}
