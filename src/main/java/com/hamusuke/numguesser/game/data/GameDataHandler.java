package com.hamusuke.numguesser.game.data;

import com.hamusuke.numguesser.network.Player;
import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.codec.StreamCodec;
import com.hamusuke.numguesser.network.codec.StreamEncoder;
import io.netty.handler.codec.EncoderException;

import java.util.function.Function;

public sealed interface GameDataHandler<V> permits GameDataHandler.Individually, GameDataHandler.Generic {
    static <V> Generic<V> of(StreamCodec<IntelligentByteBuf, V> codec) {
        return new Generic<>(codec);
    }

    static <V> Individually<V> syncIndividually(StreamCodec<IntelligentByteBuf, V> decoder, Function<Player, StreamEncoder<IntelligentByteBuf, V>> encoder) {
        return new Individually<>(new StreamCodec<>() {
            @Override
            public V decode(IntelligentByteBuf intelligentByteBuf) {
                return decoder.decode(intelligentByteBuf);
            }

            @Override
            public void encode(IntelligentByteBuf o, V v) {
                throw new EncoderException("This codec is used for individual data");
            }
        }, encoder);
    }

    StreamCodec<IntelligentByteBuf, V> codec();

    default GameData<V> create(final int id) {
        return new GameData<>(id, this);
    }

    record Generic<V>(StreamCodec<IntelligentByteBuf, V> codec) implements GameDataHandler<V> {
    }

    record Individually<V>(StreamCodec<IntelligentByteBuf, V> decoder,
                           Function<Player, StreamEncoder<IntelligentByteBuf, V>> encoder) implements GameDataHandler<V> {
        @Override
        public StreamCodec<IntelligentByteBuf, V> codec() {
            return this.decoder;
        }
    }
}
