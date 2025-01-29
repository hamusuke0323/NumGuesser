package com.hamusuke.numguesser.network.protocol.packet.login.serverbound;

import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.codec.StreamCodec;
import com.hamusuke.numguesser.network.listener.server.login.ServerLoginPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import com.hamusuke.numguesser.network.protocol.packet.PacketType;
import com.hamusuke.numguesser.network.protocol.packet.login.LoginPacketTypes;

public class LobbyJoinedNotify implements Packet<ServerLoginPacketListener> {
    public static final LobbyJoinedNotify INSTANCE = new LobbyJoinedNotify();
    public static final StreamCodec<IntelligentByteBuf, LobbyJoinedNotify> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    private LobbyJoinedNotify() {
    }

    @Override
    public PacketType<LobbyJoinedNotify> type() {
        return LoginPacketTypes.LOBBY_JOINED;
    }

    @Override
    public void handle(ServerLoginPacketListener listener) {
        listener.handleLobbyJoined(this);
    }

    @Override
    public boolean isTerminal() {
        return true;
    }
}
