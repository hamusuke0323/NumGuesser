package com.hamusuke.numguesser.network.protocol;

import com.hamusuke.numguesser.network.codec.StreamCodec;
import com.hamusuke.numguesser.network.listener.PacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;

public interface ProtocolInfo<T extends PacketListener> {
    Protocol id();

    PacketDirection direction();

    StreamCodec<ByteBuf, Packet<? super T>> codec();
}
