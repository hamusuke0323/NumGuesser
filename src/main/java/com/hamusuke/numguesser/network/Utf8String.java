package com.hamusuke.numguesser.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;

import java.nio.charset.StandardCharsets;

public class Utf8String {
    public static String read(ByteBuf buf, int maxLen) {
        int maxBytes = ByteBufUtil.utf8MaxBytes(maxLen);
        int len = VarInt.read(buf);
        if (len > maxBytes) {
            throw new DecoderException("The received encoded string buffer length is longer than maximum allowed (" + len + " > " + maxBytes + ")");
        } else if (len < 0) {
            throw new DecoderException("The received encoded string buffer length is less than zero! Weird string!");
        } else {
            int i = buf.readableBytes();
            if (len > i) {
                throw new DecoderException("Not enough bytes in buffer, expected " + len + ", but got " + i);
            } else {
                var s = buf.toString(buf.readerIndex(), len, StandardCharsets.UTF_8);
                buf.readerIndex(buf.readerIndex() + len);
                if (s.length() > maxLen) {
                    int length = s.length();
                    throw new DecoderException("The received string length is longer than maximum allowed (" + length + " > " + maxLen + ")");
                } else {
                    return s;
                }
            }
        }
    }

    public static void write(ByteBuf buf, CharSequence str, int maxLen) {
        if (str.length() > maxLen) {
            int len = str.length();
            throw new EncoderException("String too big (was " + len + " characters, max " + maxLen + ")");
        } else {
            int maxBytes = ByteBufUtil.utf8MaxBytes(str);
            var buffer = buf.alloc().buffer(maxBytes);

            try {
                int i = ByteBufUtil.writeUtf8(buffer, str);
                int maxBytes1 = ByteBufUtil.utf8MaxBytes(maxLen);
                if (i > maxBytes1) {
                    throw new EncoderException("String too big (was " + i + " bytes encoded, max " + maxBytes1 + ")");
                }

                VarInt.write(buf, i);
                buf.writeBytes(buffer);
            } finally {
                buffer.release();
            }
        }
    }
}
