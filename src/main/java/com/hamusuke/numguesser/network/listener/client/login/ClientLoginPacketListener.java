package com.hamusuke.numguesser.network.listener.client.login;

import com.hamusuke.numguesser.network.listener.client.ClientboundBasePacketListener;
import com.hamusuke.numguesser.network.protocol.Protocol;
import com.hamusuke.numguesser.network.protocol.packet.login.clientbound.EnterNameReq;
import com.hamusuke.numguesser.network.protocol.packet.login.clientbound.KeyExchangeRsp;
import com.hamusuke.numguesser.network.protocol.packet.login.clientbound.LoginCompressionNotify;
import com.hamusuke.numguesser.network.protocol.packet.login.clientbound.LoginSuccessNotify;

public interface ClientLoginPacketListener extends ClientboundBasePacketListener {
    @Override
    default Protocol protocol() {
        return Protocol.LOGIN;
    }

    void handleKeyEx(KeyExchangeRsp packet);

    void handleSuccess(LoginSuccessNotify packet);

    void handleCompression(LoginCompressionNotify packet);

    void handleEnterName(EnterNameReq packet);
}
