package com.hamusuke.numguesser.network.protocol.packet.lobby.clientbound;

import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.codec.StreamCodec;
import com.hamusuke.numguesser.network.listener.client.lobby.ClientLobbyPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import com.hamusuke.numguesser.network.protocol.packet.PacketType;
import com.hamusuke.numguesser.network.protocol.packet.lobby.LobbyPacketTypes;

public record EnterPasswordReq(int roomId, String msg) implements Packet<ClientLobbyPacketListener> {
    public static final StreamCodec<IntelligentByteBuf, EnterPasswordReq> STREAM_CODEC = Packet.codec(EnterPasswordReq::write, EnterPasswordReq::new);

    private EnterPasswordReq(IntelligentByteBuf buf) {
        this(buf.readVarInt(), buf.readString());
    }

    private void write(IntelligentByteBuf buf) {
        buf.writeVarInt(this.roomId);
        buf.writeString(this.msg);
    }

    @Override
    public PacketType<EnterPasswordReq> type() {
        return LobbyPacketTypes.ENTER_PASSWORD_REQ;
    }

    @Override
    public void handle(ClientLobbyPacketListener listener) {
        listener.handleEnterPassword(this);
    }
}
