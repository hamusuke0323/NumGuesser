package com.hamusuke.numguesser.network.codec;

import io.netty.buffer.ByteBuf;

import java.util.function.Function;

public interface StreamCodec<B, V> extends StreamDecoder<B, V>, StreamEncoder<B, V> {
    static <B, V> StreamCodec<B, V> ofMember(final StreamMemberEncoder<B, V> encoder, final StreamDecoder<B, V> decoder) {
        return new StreamCodec<>() {
            @Override
            public V decode(B b) {
                return decoder.decode(b);
            }

            @Override
            public void encode(B b, V v) {
                encoder.encode(v, b);
            }
        };
    }

    static <B, V> StreamCodec<B, V> unit(final V v) {
        return new StreamCodec<>() {
            @Override
            public V decode(B b) {
                return v;
            }

            @Override
            public void encode(B b, V v2) {
                if (!v2.equals(v)) {
                    var s = String.valueOf(v2);
                    throw new IllegalStateException("Can't encode '" + s + "', expected '" + v + "'");
                }
            }
        };
    }

    default <O extends ByteBuf> StreamCodec<O, V> mapStream(final Function<O, ? extends B> func) {
        return new StreamCodec<>() {
            @Override
            public V decode(O o) {
                var b = func.apply(o);
                return StreamCodec.this.decode(b);
            }

            @Override
            public void encode(O o, V v) {
                var b = func.apply(o);
                StreamCodec.this.encode(b, v);
            }
        };
    }
}
