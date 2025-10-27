package com.hamusuke.numguesser.game.data;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableList;
import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.codec.StreamCodec;
import com.hamusuke.numguesser.network.codec.StreamCodecs;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public class GameDataHandlerRegistry {
    private static final AtomicInteger ID = new AtomicInteger();
    private static final Supplier<Integer> NEXT_ID = ID::getAndIncrement;
    private static final BiMap<Integer, GameDataHandler<?>> HANDLERS = HashBiMap.create();
    public static final GameDataHandler<List<Integer>> VAR_INT_LIST = register(StreamCodec.of((b, integers) -> {
        b.writeList(integers, IntelligentByteBuf::writeVarInt);
    }, b -> b.readList(IntelligentByteBuf::readVarInt, ImmutableList::copyOf)));
    public static final GameDataHandler<Integer> VAR_INT = register(StreamCodecs.VAR_INT);
    public static final GameDataHandler<Boolean> BOOLEAN = register(StreamCodecs.BOOLEAN);

    private static <V> GameDataHandler<V> register(final StreamCodec<IntelligentByteBuf, V> codec) {
        final var handler = GameDataHandler.of(codec);
        HANDLERS.put(NEXT_ID.get(), handler);
        return handler;
    }

    public static GameDataHandler<?> getHandler(final int id) {
        return HANDLERS.get(id);
    }

    public static int getId(final GameDataHandler<?> handler) {
        final var map = HANDLERS.inverse();
        final var id = map.get(handler);
        if (id == null) {
            throw new IllegalArgumentException("this handler is not registered");
        }

        return id;
    }
}
