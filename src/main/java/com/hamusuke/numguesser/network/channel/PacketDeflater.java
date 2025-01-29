package com.hamusuke.numguesser.network.channel;

import com.hamusuke.numguesser.network.VarInt;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.zip.Deflater;

import static com.hamusuke.numguesser.network.channel.PacketInflater.MAXIMUM_UNCOMPRESSED_LENGTH;

public class PacketDeflater extends MessageToByteEncoder<ByteBuf> {
    private static final Logger LOGGER = LogManager.getLogger();
    private final byte[] encodeBuf = new byte[8192];
    private final Deflater deflater;
    private int threshold;

    public PacketDeflater(int threshold) {
        this.threshold = threshold;
        this.deflater = new Deflater();
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, ByteBuf out) {
        int i = msg.readableBytes();
        if (i > MAXIMUM_UNCOMPRESSED_LENGTH) {
            throw new IllegalArgumentException("Packet too big (is " + i + ", should be less than " + MAXIMUM_UNCOMPRESSED_LENGTH + ")");
        }

        if (i < this.threshold) {
            VarInt.write(out, 0);
            out.writeBytes(msg);
        } else {
            byte[] abyte = new byte[i];
            msg.readBytes(abyte);
            VarInt.write(out, abyte.length);
            this.deflater.setInput(abyte, 0, i);
            this.deflater.finish();

            while (!this.deflater.finished()) {
                int j = this.deflater.deflate(this.encodeBuf);
                out.writeBytes(this.encodeBuf, 0, j);
            }

            this.deflater.reset();
        }
    }

    public int getThreshold() {
        return this.threshold;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }
}
