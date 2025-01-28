package com.hamusuke.numguesser.network.protocol.packet.lobby.serverbound;

import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.codec.StreamCodec;
import com.hamusuke.numguesser.network.listener.server.lobby.ServerLobbyPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import com.hamusuke.numguesser.network.protocol.packet.PacketType;
import com.hamusuke.numguesser.network.protocol.packet.lobby.LobbyPacketTypes;

public class LobbyDisconnectReq implements Packet<ServerLobbyPacketListener> {
    public static final LobbyDisconnectReq INSTANCE = new LobbyDisconnectReq();
    public static final StreamCodec<IntelligentByteBuf, LobbyDisconnectReq> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    private LobbyDisconnectReq() {
    }

    @Override
    public PacketType<LobbyDisconnectReq> type() {
        return LobbyPacketTypes.DISCONNECT_REQ;
    }

    @Override
    public void handle(ServerLobbyPacketListener listener) {
        listener.handleDisconnect(this);
    }
}
