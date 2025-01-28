package com.hamusuke.numguesser.network.protocol.packet;

import io.netty.handler.codec.EncoderException;

public class SkipPacketException extends EncoderException {
    public SkipPacketException(Throwable cause) {
        super(cause);
    }
}
