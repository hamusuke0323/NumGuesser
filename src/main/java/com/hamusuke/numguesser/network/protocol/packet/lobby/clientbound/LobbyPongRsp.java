package com.hamusuke.numguesser.network.protocol.packet.lobby.clientbound;

import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.codec.StreamCodec;
import com.hamusuke.numguesser.network.listener.client.lobby.ClientLobbyPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import com.hamusuke.numguesser.network.protocol.packet.PacketType;
import com.hamusuke.numguesser.network.protocol.packet.lobby.LobbyPacketTypes;

public class LobbyPongRsp implements Packet<ClientLobbyPacketListener> {
    public static final LobbyPongRsp INSTANCE = new LobbyPongRsp();
    public static final StreamCodec<IntelligentByteBuf, LobbyPongRsp> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    private LobbyPongRsp() {
    }

    @Override
    public PacketType<LobbyPongRsp> type() {
        return LobbyPacketTypes.PONG;
    }

    @Override
    public void handle(ClientLobbyPacketListener listener) {
        listener.handlePong(this);
    }
}
