package com.hamusuke.numguesser.network.codec;

import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;

public interface StreamCodecs {
    StreamCodec<IntelligentByteBuf, Integer> INTEGER = StreamCodec.of(IntelligentByteBuf::writeInt, IntelligentByteBuf::readInt);
    StreamCodec<IntelligentByteBuf, Long> LONG = StreamCodec.of(IntelligentByteBuf::writeLong, IntelligentByteBuf::readLong);
    StreamCodec<IntelligentByteBuf, Float> FLOAT = StreamCodec.of(IntelligentByteBuf::writeFloat, IntelligentByteBuf::readFloat);
    StreamCodec<IntelligentByteBuf, Double> DOUBLE = StreamCodec.of(IntelligentByteBuf::writeDouble, IntelligentByteBuf::readDouble);
    StreamCodec<IntelligentByteBuf, String> STRING = StreamCodec.of(IntelligentByteBuf::writeString, IntelligentByteBuf::readString);
    StreamCodec<IntelligentByteBuf, Integer> VAR_INT = StreamCodec.of(IntelligentByteBuf::writeVarInt, IntelligentByteBuf::readVarInt);
    StreamCodec<IntelligentByteBuf, Long> VAR_LONG = StreamCodec.of(IntelligentByteBuf::writeVarLong, IntelligentByteBuf::readVarLong);
    StreamCodec<IntelligentByteBuf, Boolean> BOOLEAN = StreamCodec.of(IntelligentByteBuf::writeBoolean, IntelligentByteBuf::readBoolean);
    StreamCodec<IntelligentByteBuf, Byte> BYTE = StreamCodec.of((o, aByte) -> o.writeByte(aByte), IntelligentByteBuf::readByte);
}
