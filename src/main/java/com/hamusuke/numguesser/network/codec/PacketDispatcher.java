package com.hamusuke.numguesser.network.codec;

import com.hamusuke.numguesser.network.VarInt;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class PacketDispatcher<B extends ByteBuf, V, T> implements StreamCodec<B, V> {
    private static final int UNKNOWN_TYPE = -1;
    private final Function<V, ? extends T> typeGetter;
    private final List<Entry<B, V, T>> byId;
    private final Object2IntMap<T> toId;

    PacketDispatcher(Function<V, ? extends T> typeGetter, List<Entry<B, V, T>> byId, Object2IntMap<T> toId) {
        this.typeGetter = typeGetter;
        this.byId = byId;
        this.toId = toId;
    }

    public static <B extends ByteBuf, V, T> Builder<B, V, T> builder(Function<V, ? extends T> typeGetter) {
        return new Builder<>(typeGetter);
    }

    @Override
    public V decode(B buf) {
        int id = VarInt.read(buf);
        if (id >= 0 && id < this.byId.size()) {
            var e = this.byId.get(id);

            try {
                return e.serializer.decode(buf);
            } catch (Exception exception) {
                throw new DecoderException("Failed to decode packet '" + e.type + "'", exception);
            }
        } else {
            throw new DecoderException("Received unknown packet id " + id);
        }
    }

    @Override
    public void encode(B buf, V packet) {
        var clazz = this.typeGetter.apply(packet);
        int id = this.toId.getOrDefault(clazz, UNKNOWN_TYPE);
        if (id == UNKNOWN_TYPE) {
            throw new EncoderException("Sending unknown packet '" + clazz + "'");
        } else {
            VarInt.write(buf, id);
            var e = this.byId.get(id);

            try {
                var codec = (StreamCodec<? super B, V>) e.serializer;
                codec.encode(buf, packet);
            } catch (Exception ex) {
                throw new EncoderException("Failed to encode packet '" + clazz + "'", ex);
            }
        }
    }

    public static class Builder<B extends ByteBuf, V, T> {
        private final List<Entry<B, V, T>> entries = new ArrayList<>();
        private final Function<V, ? extends T> typeGetter;

        Builder(Function<V, ? extends T> typeGetter) {
            this.typeGetter = typeGetter;
        }

        public Builder<B, V, T> add(T clazz, StreamCodec<? super B, ? extends V> codec) {
            this.entries.add(new Entry<>(codec, clazz));
            return this;
        }

        public PacketDispatcher<B, V, T> build() {
            Object2IntOpenHashMap<T> map = new Object2IntOpenHashMap<>();
            map.defaultReturnValue(-2);

            for (var e : this.entries) {
                int size = map.size();
                int i = map.putIfAbsent(e.type, size);
                if (i != -2) {
                    throw new IllegalStateException("Duplicate registration for type " + e.type);
                }
            }

            return new PacketDispatcher<>(this.typeGetter, List.copyOf(this.entries), map);
        }
    }

    record Entry<B, V, T>(StreamCodec<? super B, ? extends V> serializer, T type) {
    }
}
