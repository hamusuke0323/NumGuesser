package com.hamusuke.numguesser.network.protocol.packet.lobby.serverbound;

import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.codec.StreamCodec;
import com.hamusuke.numguesser.network.listener.server.lobby.ServerLobbyPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import com.hamusuke.numguesser.network.protocol.packet.PacketType;
import com.hamusuke.numguesser.network.protocol.packet.lobby.LobbyPacketTypes;

public class LobbyPingReq implements Packet<ServerLobbyPacketListener> {
    public static final LobbyPingReq INSTANCE = new LobbyPingReq();
    public static final StreamCodec<IntelligentByteBuf, LobbyPingReq> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    private LobbyPingReq() {
    }

    @Override
    public PacketType<LobbyPingReq> type() {
        return LobbyPacketTypes.PING;
    }

    @Override
    public void handle(ServerLobbyPacketListener listener) {
        listener.handlePing(this);
    }
}
