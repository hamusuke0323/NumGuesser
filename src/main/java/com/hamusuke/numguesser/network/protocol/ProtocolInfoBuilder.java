package com.hamusuke.numguesser.network.protocol;

import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.codec.StreamCodec;
import com.hamusuke.numguesser.network.listener.PacketListener;
import com.hamusuke.numguesser.network.listener.client.ClientboundPacketListener;
import com.hamusuke.numguesser.network.listener.server.ServerboundPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import com.hamusuke.numguesser.network.protocol.packet.PacketType;
import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class ProtocolInfoBuilder<T extends PacketListener, B extends ByteBuf> {
    private final Protocol protocol;
    private final PacketDirection direction;
    private final List<CodecEntry<T, ?, B>> codecs = new ArrayList<>();

    public ProtocolInfoBuilder(Protocol protocol, PacketDirection direction) {
        this.protocol = protocol;
        this.direction = direction;
    }

    private static <L extends PacketListener> ProtocolInfo<L> protocol(Protocol protocol, PacketDirection direction, Consumer<ProtocolInfoBuilder<L, IntelligentByteBuf>> consumer) {
        ProtocolInfoBuilder<L, IntelligentByteBuf> builder = new ProtocolInfoBuilder<>(protocol, direction);
        consumer.accept(builder);
        return builder.build(IntelligentByteBuf::new);
    }

    public static <T extends ServerboundPacketListener> ProtocolInfo<T> serverboundProtocol(Protocol protocol, Consumer<ProtocolInfoBuilder<T, IntelligentByteBuf>> consumer) {
        return protocol(protocol, PacketDirection.SERVERBOUND, consumer);
    }

    public static <T extends ClientboundPacketListener> ProtocolInfo<T> clientboundProtocol(Protocol protocol, Consumer<ProtocolInfoBuilder<T, IntelligentByteBuf>> consumer) {
        return protocol(protocol, PacketDirection.CLIENTBOUND, consumer);
    }

    public <P extends Packet<? super T>> ProtocolInfoBuilder<T, B> addPacket(PacketType<P> type, StreamCodec<? super B, P> codec) {
        this.codecs.add(new CodecEntry<>(type, codec));
        return this;
    }

    private StreamCodec<ByteBuf, Packet<? super T>> buildPacketCodec(Function<ByteBuf, B> bufTransformer, List<CodecEntry<T, ?, B>> codecs) {
        ProtocolCodecBuilder<ByteBuf, T> builder = new ProtocolCodecBuilder<>(this.direction);

        for (var e : codecs) {
            e.addToBuilder(builder, bufTransformer);
        }

        return builder.build();
    }

    public ProtocolInfo<T> build(Function<ByteBuf, B> bufTransformer) {
        return new Implementation<>(this.protocol, this.direction, this.buildPacketCodec(bufTransformer, this.codecs));
    }

    record CodecEntry<T extends PacketListener, P extends Packet<? super T>, B extends ByteBuf>(
            PacketType<P> type, StreamCodec<? super B, P> serializer) {

        public void addToBuilder(ProtocolCodecBuilder<ByteBuf, T> builder, Function<ByteBuf, B> bufTransformer) {
            StreamCodec<ByteBuf, P> codec = this.serializer.mapStream(bufTransformer);
            builder.add(this.type, codec);
        }
    }

    record Implementation<L extends PacketListener>(Protocol id, PacketDirection direction,
                                                    StreamCodec<ByteBuf, Packet<? super L>> codec) implements ProtocolInfo<L> {
    }
}
