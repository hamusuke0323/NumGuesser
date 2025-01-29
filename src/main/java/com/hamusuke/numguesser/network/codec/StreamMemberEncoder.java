package com.hamusuke.numguesser.network.codec;

public interface StreamMemberEncoder<O, T> {
    void encode(T t, O o);
}
