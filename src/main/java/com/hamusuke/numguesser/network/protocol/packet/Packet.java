package com.hamusuke.numguesser.network.protocol.packet;

import com.hamusuke.numguesser.network.codec.StreamCodec;
import com.hamusuke.numguesser.network.codec.StreamDecoder;
import com.hamusuke.numguesser.network.codec.StreamMemberEncoder;
import com.hamusuke.numguesser.network.listener.PacketListener;
import io.netty.buffer.ByteBuf;

public interface Packet<T extends PacketListener> {
    static <B extends ByteBuf, T extends Packet<?>> StreamCodec<B, T> codec(StreamMemberEncoder<B, T> encoder, StreamDecoder<B, T> decoder) {
        return StreamCodec.ofMember(encoder, decoder);
    }

    void handle(T listener);

    PacketType<? extends Packet<T>> type();

    default boolean isSkippable() {
        return false;
    }

    default boolean isTerminal() {
        return false;
    }
}
