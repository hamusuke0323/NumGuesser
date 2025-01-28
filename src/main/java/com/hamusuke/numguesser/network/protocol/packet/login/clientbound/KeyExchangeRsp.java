package com.hamusuke.numguesser.network.protocol.packet.login.clientbound;

import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.codec.StreamCodec;
import com.hamusuke.numguesser.network.encryption.NetworkEncryptionUtil;
import com.hamusuke.numguesser.network.listener.client.login.ClientLoginPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import com.hamusuke.numguesser.network.protocol.packet.PacketType;
import com.hamusuke.numguesser.network.protocol.packet.login.LoginPacketTypes;

import java.security.PublicKey;

public record KeyExchangeRsp(byte[] publicKey,
                             byte[] nonce) implements Packet<ClientLoginPacketListener> {
    public static final StreamCodec<IntelligentByteBuf, KeyExchangeRsp> STREAM_CODEC = Packet.codec(KeyExchangeRsp::write, KeyExchangeRsp::new);

    private KeyExchangeRsp(IntelligentByteBuf buf) {
        this(buf.readByteArray(), buf.readByteArray());
    }

    private void write(IntelligentByteBuf buf) {
        buf.writeByteArray(this.publicKey);
        buf.writeByteArray(this.nonce);
    }

    @Override
    public PacketType<KeyExchangeRsp> type() {
        return LoginPacketTypes.KEY_EXCHANGE_RSP;
    }

    @Override
    public void handle(ClientLoginPacketListener listener) {
        listener.handleKeyEx(this);
    }

    public PublicKey getPublicKey() throws Exception {
        return NetworkEncryptionUtil.readEncodedPublicKey(this.publicKey);
    }
}
