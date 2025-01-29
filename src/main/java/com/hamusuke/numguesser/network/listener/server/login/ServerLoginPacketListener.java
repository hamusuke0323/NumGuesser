package com.hamusuke.numguesser.network.listener.server.login;

import com.hamusuke.numguesser.network.listener.server.ServerboundBasePacketListener;
import com.hamusuke.numguesser.network.protocol.Protocol;
import com.hamusuke.numguesser.network.protocol.packet.login.serverbound.EncryptionSetupReq;
import com.hamusuke.numguesser.network.protocol.packet.login.serverbound.EnterNameRsp;
import com.hamusuke.numguesser.network.protocol.packet.login.serverbound.KeyExchangeReq;
import com.hamusuke.numguesser.network.protocol.packet.login.serverbound.LobbyJoinedNotify;

public interface ServerLoginPacketListener extends ServerboundBasePacketListener {
    @Override
    default Protocol protocol() {
        return Protocol.LOGIN;
    }

    void handleKeyEx(KeyExchangeReq packet);

    void handleEncryption(EncryptionSetupReq packet);

    void handleEnterName(EnterNameRsp packet);

    void handleLobbyJoined(LobbyJoinedNotify packet);
}
