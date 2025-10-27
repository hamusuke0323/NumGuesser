package com.hamusuke.numguesser.game.data;

import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.codec.StreamCodec;

public interface GameDataHandler<V> {
    static <V> GameDataHandler<V> of(StreamCodec<IntelligentByteBuf, V> codec) {
        return () -> codec;
    }

    StreamCodec<IntelligentByteBuf, V> codec();

    default GameData<V> create(final int id) {
        return new GameData<>(id, this);
    }
}
