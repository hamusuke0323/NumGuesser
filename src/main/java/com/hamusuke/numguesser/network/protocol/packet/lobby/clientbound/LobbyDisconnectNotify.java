package com.hamusuke.numguesser.network.protocol.packet.lobby.clientbound;

import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.codec.StreamCodec;
import com.hamusuke.numguesser.network.listener.client.lobby.ClientLobbyPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import com.hamusuke.numguesser.network.protocol.packet.PacketType;
import com.hamusuke.numguesser.network.protocol.packet.lobby.LobbyPacketTypes;

public record LobbyDisconnectNotify(String msg) implements Packet<ClientLobbyPacketListener> {
    public static final StreamCodec<IntelligentByteBuf, LobbyDisconnectNotify> STREAM_CODEC = Packet.codec(LobbyDisconnectNotify::write, LobbyDisconnectNotify::new);

    private LobbyDisconnectNotify(IntelligentByteBuf buf) {
        this(buf.readString());
    }

    private void write(IntelligentByteBuf buf) {
        buf.writeString(this.msg);
    }

    @Override
    public PacketType<LobbyDisconnectNotify> type() {
        return LobbyPacketTypes.DISCONNECT;
    }

    @Override
    public void handle(ClientLobbyPacketListener listener) {
        listener.handleDisconnectPacket(this);
    }
}
