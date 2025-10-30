package com.hamusuke.numguesser.game.data;

import com.google.common.collect.Maps;
import com.hamusuke.numguesser.network.Player;
import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import io.netty.handler.codec.DecoderException;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public class GameDataSyncer {
    private static final AtomicInteger CURRENT_ID = new AtomicInteger();
    private static final Supplier<Integer> NEXT_ID = CURRENT_ID::getAndIncrement;
    private final Map<Integer, Entry<?>> entries = Maps.newHashMap();

    public static <V> GameData<V> registerData(final GameDataHandler<V> handler) {
        return handler.create(NEXT_ID.get());
    }

    public <V> void define(final GameData<V> data, final V value) {
        if (this.entries.containsKey(data.id())) {
            throw new IllegalArgumentException("this entry already defined");
        }

        this.entries.put(data.id(), new Entry<>(data, value));
    }

    public void remove(final GameData<?> data) {
        this.entries.remove(data.id());
    }

    public <V> V get(final GameData<V> data) {
        return this.getEntry(data).getValue();
    }

    public <V> void set(final GameData<V> data, final V value) {
        final var e = this.getEntry(data);
        final var tmp = e.getValue();
        if (tmp.equals(value)) {
            return;
        }

        e.setValue(value);
        e.markDirty();
    }

    public <V> Entry<V> getEntry(final GameData<V> data) {
        return this.getEntry(data.id());
    }

    public <V> Entry<V> getEntry(final int id) {
        return (Entry<V>) this.entries.get(id);
    }

    public <V> void copyEntryFrom(final SerializedData<V> data) {
        final var entry = (Entry<V>) this.entries.get(data.entryId);
        if (entry == null) {
            throw new IllegalArgumentException("unknown entry id: " + data.entryId);
        }

        if (entry.data.handler() != data.handler) {
            throw new IllegalArgumentException("handler mismatch: " + entry.data.handler() + " != " + data.handler);
        }

        entry.setValue(data.value);
    }

    public <V> SerializedData<V> toSerialized(final GameData<V> data) {
        final var entry = this.getEntry(data);
        return new SerializedData<>(data.id(), data.handler(), entry.getValue());
    }

    public record SerializedData<V>(int entryId, GameDataHandler<V> handler, V value) {
        public static <V> SerializedData<V> from(final IntelligentByteBuf buf) {
            final int handlerId = buf.readVarInt();
            final var handler = (GameDataHandler<V>) GameDataHandlerRegistry.getHandler(handlerId);
            if (handler == null) {
                throw new DecoderException("Unknown handler id: " + handlerId);
            }

            final int entryId = buf.readVarInt();
            final var value = handler.codec().decode(buf);
            return new SerializedData<>(entryId, handler, value);
        }

        public void writeTo(final Player player, final IntelligentByteBuf buf) {
            buf.writeVarInt(GameDataHandlerRegistry.getId(this.handler));
            buf.writeVarInt(this.entryId);

            switch (this.handler) {
                case GameDataHandler.Generic(final var codec) -> codec.encode(buf, this.value);
                case GameDataHandler.Individually(final var __, final var encoder) ->
                        encoder.apply(player).encode(buf, this.value);
            }
        }
    }

    public static class Entry<V> {
        private final GameData<V> data;
        private V value;
        private boolean dirty;

        Entry(final GameData<V> data, final V value) {
            this.data = data;
            this.value = value;
        }

        public void markDirty() {
            this.dirty = true;
        }

        public void clearDirty() {
            this.dirty = false;
        }

        public boolean isDirty() {
            return this.dirty;
        }

        public GameData<V> getData() {
            return this.data;
        }

        public V getValue() {
            return this.value;
        }

        public void setValue(final V value) {
            this.value = value;
        }
    }
}
