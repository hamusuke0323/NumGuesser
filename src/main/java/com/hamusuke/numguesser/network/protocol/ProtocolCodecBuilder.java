package com.hamusuke.numguesser.network.protocol;

import com.hamusuke.numguesser.network.codec.PacketDispatcher;
import com.hamusuke.numguesser.network.codec.StreamCodec;
import com.hamusuke.numguesser.network.listener.PacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import com.hamusuke.numguesser.network.protocol.packet.PacketType;
import io.netty.buffer.ByteBuf;

public class ProtocolCodecBuilder<B extends ByteBuf, L extends PacketListener> {
    private final PacketDispatcher.Builder<B, Packet<? super L>, PacketType<?>> dispatchBuilder = PacketDispatcher.builder(Packet::type);
    private final PacketDirection direction;

    public ProtocolCodecBuilder(PacketDirection direction) {
        this.direction = direction;
    }

    public <T extends Packet<? super L>> ProtocolCodecBuilder<B, L> add(PacketType<T> type, StreamCodec<? super B, T> codec) {
        if (type.direction() != this.direction) {
            var s = String.valueOf(type);
            throw new IllegalArgumentException("Invalid packet flow for packet " + s + ", expected " + this.direction.name());
        } else {
            this.dispatchBuilder.add(type, codec);
            return this;
        }
    }

    public StreamCodec<B, Packet<? super L>> build() {
        return this.dispatchBuilder.build();
    }
}
