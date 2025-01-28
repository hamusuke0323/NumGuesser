package com.hamusuke.numguesser.network.codec;

public interface StreamEncoder<O, T> {
    void encode(O o, T t);
}
