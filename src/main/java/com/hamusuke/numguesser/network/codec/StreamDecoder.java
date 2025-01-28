package com.hamusuke.numguesser.network.codec;

public interface StreamDecoder<B, T> {
    T decode(B b);
}
