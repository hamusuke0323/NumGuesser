package com.hamusuke.numguesser.network.listener.client.login;

import com.hamusuke.numguesser.network.listener.client.ClientboundPacketListener;
import com.hamusuke.numguesser.network.protocol.Protocol;
import com.hamusuke.numguesser.network.protocol.packet.login.clientbound.*;

public interface ClientLoginPacketListener extends ClientboundPacketListener {
    @Override
    default Protocol protocol() {
        return Protocol.LOGIN;
    }

    void handleKeyEx(KeyExchangeRsp packet);

    void handleSuccess(LoginSuccessNotify packet);

    void handleDisconnect(LoginDisconnectNotify packet);

    void handleCompression(LoginCompressionNotify packet);

    void handleEnterName(EnterNameReq packet);
}
