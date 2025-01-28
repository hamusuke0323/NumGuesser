package com.hamusuke.numguesser.network.protocol.packet.lobby.clientbound;

import com.google.common.collect.ImmutableList;
import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.codec.StreamCodec;
import com.hamusuke.numguesser.network.listener.client.lobby.ClientLobbyPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import com.hamusuke.numguesser.network.protocol.packet.PacketType;
import com.hamusuke.numguesser.network.protocol.packet.lobby.LobbyPacketTypes;
import com.hamusuke.numguesser.room.RoomInfo;

import java.util.List;

public record RoomListNotify(List<RoomInfo> infoList) implements Packet<ClientLobbyPacketListener> {
    public static final StreamCodec<IntelligentByteBuf, RoomListNotify> STREAM_CODEC = Packet.codec(RoomListNotify::write, RoomListNotify::new);

    private RoomListNotify(IntelligentByteBuf buf) {
        this(buf.<List<RoomInfo>, RoomInfo>readList(RoomInfo::new, ImmutableList::copyOf));
    }

    private void write(IntelligentByteBuf buf) {
        buf.writeList(this.infoList, RoomInfo::writeTo);
    }

    @Override
    public PacketType<RoomListNotify> type() {
        return LobbyPacketTypes.ROOM_LIST;
    }

    @Override
    public void handle(ClientLobbyPacketListener listener) {
        listener.handleRoomList(this);
    }
}
