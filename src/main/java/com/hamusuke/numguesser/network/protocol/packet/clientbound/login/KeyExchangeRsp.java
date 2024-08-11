package com.hamusuke.numguesser.network.protocol.packet.clientbound.login;

import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.encryption.NetworkEncryptionUtil;
import com.hamusuke.numguesser.network.listener.client.login.ClientLoginPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;

import java.security.PublicKey;

public record KeyExchangeRsp(byte[] publicKey,
                             byte[] nonce) implements Packet<ClientLoginPacketListener> {
    public KeyExchangeRsp(IntelligentByteBuf buf) {
        this(buf.readByteArray(), buf.readByteArray());
    }

    public void write(IntelligentByteBuf buf) {
        buf.writeByteArray(this.publicKey);
        buf.writeByteArray(this.nonce);
    }

    @Override
    public void handle(ClientLoginPacketListener listener) {
        listener.handleKeyEx(this);
    }

    public PublicKey getPublicKey() throws Exception {
        return NetworkEncryptionUtil.readEncodedPublicKey(this.publicKey);
    }
}
