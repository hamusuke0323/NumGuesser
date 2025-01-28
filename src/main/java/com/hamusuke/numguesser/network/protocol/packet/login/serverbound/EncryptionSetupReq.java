package com.hamusuke.numguesser.network.protocol.packet.login.serverbound;

import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.codec.StreamCodec;
import com.hamusuke.numguesser.network.encryption.NetworkEncryptionUtil;
import com.hamusuke.numguesser.network.listener.server.login.ServerLoginPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import com.hamusuke.numguesser.network.protocol.packet.PacketType;
import com.hamusuke.numguesser.network.protocol.packet.login.LoginPacketTypes;

import javax.crypto.SecretKey;
import java.security.PrivateKey;
import java.security.PublicKey;

public record EncryptionSetupReq(byte[] encryptedSecretKey,
                                 byte[] encryptedNonce) implements Packet<ServerLoginPacketListener> {
    public static final StreamCodec<IntelligentByteBuf, EncryptionSetupReq> STREAM_CODEC = Packet.codec(EncryptionSetupReq::write, EncryptionSetupReq::new);

    public EncryptionSetupReq(SecretKey secretKey, PublicKey publicKey, byte[] nonce) throws Exception {
        this(NetworkEncryptionUtil.encrypt(publicKey, secretKey.getEncoded()), NetworkEncryptionUtil.encrypt(publicKey, nonce));
    }

    private EncryptionSetupReq(IntelligentByteBuf buf) {
        this(buf.readByteArray(), buf.readByteArray());
    }

    private void write(IntelligentByteBuf buf) {
        buf.writeByteArray(this.encryptedSecretKey);
        buf.writeByteArray(this.encryptedNonce);
    }

    @Override
    public PacketType<EncryptionSetupReq> type() {
        return LoginPacketTypes.ENCRYPTION_SETUP;
    }

    @Override
    public void handle(ServerLoginPacketListener listener) {
        listener.handleEncryption(this);
    }

    public SecretKey decryptSecretKey(PrivateKey privateKey) throws Exception {
        return NetworkEncryptionUtil.decryptSecretKey(privateKey, this.encryptedSecretKey);
    }

    public byte[] decryptNonce(PrivateKey privateKey) throws Exception {
        return NetworkEncryptionUtil.decrypt(privateKey, this.encryptedNonce);
    }
}
