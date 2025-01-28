package com.hamusuke.numguesser.network.protocol.packet.login.clientbound;

import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.codec.StreamCodec;
import com.hamusuke.numguesser.network.listener.client.login.ClientLoginPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import com.hamusuke.numguesser.network.protocol.packet.PacketType;
import com.hamusuke.numguesser.network.protocol.packet.login.LoginPacketTypes;
import com.hamusuke.numguesser.server.network.ServerPlayer;

public record LoginSuccessNotify(int id, String name) implements Packet<ClientLoginPacketListener> {
    public static final StreamCodec<IntelligentByteBuf, LoginSuccessNotify> STREAM_CODEC = Packet.codec(LoginSuccessNotify::write, LoginSuccessNotify::new);

    public LoginSuccessNotify(ServerPlayer serverPlayer) {
        this(serverPlayer.getId(), serverPlayer.getName());
    }

    private LoginSuccessNotify(IntelligentByteBuf byteBuf) {
        this(byteBuf.readVarInt(), byteBuf.readString());
    }

    private void write(IntelligentByteBuf byteBuf) {
        byteBuf.writeVarInt(this.id);
        byteBuf.writeString(this.name);
    }

    @Override
    public PacketType<LoginSuccessNotify> type() {
        return LoginPacketTypes.LOGIN_SUCCESS;
    }

    @Override
    public boolean isTerminal() {
        return true;
    }

    @Override
    public void handle(ClientLoginPacketListener listener) {
        listener.handleSuccess(this);
    }
}
