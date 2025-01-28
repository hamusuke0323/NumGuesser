package com.hamusuke.numguesser.network.protocol.packet.common.clientbound;

import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.codec.StreamCodec;
import com.hamusuke.numguesser.network.listener.client.main.ClientCommonPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import com.hamusuke.numguesser.network.protocol.packet.PacketType;
import com.hamusuke.numguesser.network.protocol.packet.common.CommonPacketTypes;

public record RoomOwnerChangeNotify(int id) implements Packet<ClientCommonPacketListener> {
    public static final StreamCodec<IntelligentByteBuf, RoomOwnerChangeNotify> STREAM_CODEC = Packet.codec(RoomOwnerChangeNotify::write, RoomOwnerChangeNotify::new);

    private RoomOwnerChangeNotify(IntelligentByteBuf buf) {
        this(buf.readVarInt());
    }

    private void write(IntelligentByteBuf buf) {
        buf.writeVarInt(this.id);
    }

    @Override
    public PacketType<RoomOwnerChangeNotify> type() {
        return CommonPacketTypes.ROOM_OWNER_CHANGE;
    }

    @Override
    public void handle(ClientCommonPacketListener listener) {
        listener.handleRoomOwnerChange(this);
    }
}
