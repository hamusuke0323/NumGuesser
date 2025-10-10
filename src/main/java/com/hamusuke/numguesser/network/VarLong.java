package com.hamusuke.numguesser.network;

import io.netty.buffer.ByteBuf;

public class VarLong {
    private static final int MAX_VARLONG_SIZE = 10;
    private static final int DATA_BITS_MASK = 127;
    private static final int CONTINUATION_BIT_MASK = 128;
    private static final int DATA_BITS_PER_BYTE = 7;

    public static int getByteSize(long l) {
        for (int i = 1; i < MAX_VARLONG_SIZE; ++i) {
            if ((l & -1L << i * DATA_BITS_PER_BYTE) == 0L) {
                return i;
            }
        }

        return MAX_VARLONG_SIZE;
    }

    public static boolean hasContinuationBit(byte b) {
        return (b & CONTINUATION_BIT_MASK) == CONTINUATION_BIT_MASK;
    }

    public static long read(ByteBuf buf) {
        long l = 0L;
        int i = 0;

        byte b;
        do {
            b = buf.readByte();
            l |= (long) (b & DATA_BITS_MASK) << i++ * DATA_BITS_PER_BYTE;
            if (i > 10) {
                throw new RuntimeException("VarLong too big");
            }
        } while (hasContinuationBit(b));

        return l;
    }

    public static void write(ByteBuf buf, long l) {
        while ((l & -CONTINUATION_BIT_MASK) != 0L) {
            buf.writeByte((int) (l & DATA_BITS_MASK) | CONTINUATION_BIT_MASK);
            l >>>= DATA_BITS_PER_BYTE;
        }

        buf.writeByte((int) l);
    }
}
