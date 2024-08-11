package com.hamusuke.numguesser.network.protocol.packet.clientbound.lobby;

import com.google.common.collect.ImmutableList;
import com.hamusuke.numguesser.room.RoomInfo;
import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.listener.client.lobby.ClientLobbyPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import com.hamusuke.numguesser.room.RoomInfo;

import java.util.List;

public record RoomListNotify(List<RoomInfo> infoList) implements Packet<ClientLobbyPacketListener> {
    public RoomListNotify(IntelligentByteBuf buf) {
        this(buf.<List<RoomInfo>, RoomInfo>readList(RoomInfo::new, ImmutableList::copyOf));
    }

    @Override
    public void write(IntelligentByteBuf buf) {
        buf.writeList(this.infoList, RoomInfo::writeTo);
    }

    @Override
    public void handle(ClientLobbyPacketListener listener) {
        listener.handleRoomList(this);
    }
}
