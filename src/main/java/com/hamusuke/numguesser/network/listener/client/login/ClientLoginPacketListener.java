package com.hamusuke.numguesser.network.listener.client.login;

import com.hamusuke.numguesser.network.listener.PacketListener;
import com.hamusuke.numguesser.network.protocol.packet.clientbound.login.*;

public interface ClientLoginPacketListener extends PacketListener {
    void handleKeyEx(KeyExchangeRsp packet);

    void handleSuccess(LoginSuccessNotify packet);

    void handleDisconnect(LoginDisconnectNotify packet);

    void handleCompression(LoginCompressionNotify packet);

    void handleEnterName(EnterNameReq packet);
}
